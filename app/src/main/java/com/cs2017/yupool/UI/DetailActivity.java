package com.cs2017.yupool.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.R;
import com.cs2017.yupool.RoutePoint;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";
    private PoolItem item;
    /*
    private ImageView profileIV;
    private TextView fromTV;
    private TextView toTV;
    private TextView timeTV;
    private TextView commentTV;
    */
    private Button applypoolBT;

    private RelativeLayout mapLayout;
    private TMapView mapView;

    private TMapData tMapData;
    private TMapMarkerItem centerPoint;
    private LinearLayout searchBarLayout;
    private TextView addressTV;
    private String address;


    private Bitmap fromIcon;
    private Bitmap fromSetIcon;
    private Bitmap toIcon;
    private Bitmap toSetIcon;

    private Button fromBT;
    private TextView fromTV;
    private Button toBT;
    private TextView toTV;

    private LinearLayout priceLayout;
    private TextView prictTV;


    private RoutePoint guestRoute;

    private boolean isStartEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        item = (PoolItem) getIntent().getSerializableExtra("item");

        guestRoute = new RoutePoint();
        getSupportActionBar().setTitle("카풀 신청하기");
        applypoolBT = findViewById(R.id.applypoolBT);
        applypoolBT.setOnClickListener(this);
        searchBarLayout = findViewById(R.id.searchBar);
        addressTV = findViewById(R.id.address);

        fromBT = findViewById(R.id.fromBT);
        fromBT.setOnClickListener(this);
        fromTV = findViewById(R.id.fromTV);
        toBT = findViewById(R.id.toBT);
        toBT.setOnClickListener(this);
        toTV = findViewById(R.id.toTV);
        priceLayout = findViewById(R.id.priceLayout);
        prictTV = findViewById(R.id.priceTV);

        /*
        profileIV = findViewById(R.id.profileIV);
        fromTV = findViewById(R.id.startplaceTV);
        toTV = findViewById(R.id.endplaceTV);
        timeTV = findViewById(R.id.starttimeTV);
        commentTV = findViewById(R.id.commentTV);
        applypoolBT = findViewById(R.id.applypoolBT);


        Glide.with(this).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getId()+".jpg")
                .bitmapTransform(new CropCircleTransformation(this))
                .into(profileIV);
        fromTV.setText(item.getFromName());
        toTV.setText(item.getToName());
        commentTV.setText(item.getComment());
        timeTV.setText(item.getStartDate());


        applypoolBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"신청되었습니다",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
*/
        initMapView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.detail){
            Toast.makeText(getApplicationContext(),"디테일",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMapView(){
        tMapData = new TMapData();
        mapLayout = (RelativeLayout)findViewById(R.id.mapView);
        mapView = new TMapView(getApplicationContext());
        mapView.setSKPMapApiKey(getString(R.string.SK_Key));
        // 언어 설정
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        // 중심 좌표 이동 (경도,위도)
        //mapView.setCenterPoint(128.754573,35.830514);
        // 현재위치로 표시될 좌표의 경도,위도 표시(경도,위도)
        //mapView.setLocationPoint(128.754573,35.830514);
        // 현재 위치로 표시될 아이콘 설정
        //mapView.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.other));
        mapView.setIconVisibility(false);
        // 지도 축적 레벨 설정 7-19레벨 설정 가능
        mapView.setZoomLevel(13);
        // 지도 한단계 줌 인 / 아웃
        //mapView.MapZoomIn();
        //mapView.MapZoomOut();
        /*
            지도 타입 선택
            MAPTYPE_STANDARD: 일반지도 / MAPTYPE_TRAFFIC: 실시간 교통지도
         */
        mapView.setMapType(TMapView.MAPTYPE_HYBRID);
        // 단말기 방향에 따라 움직이는 나침반 모드로 설정
        mapView.setCompassMode(false);
        // 화면 중심을 단말의 현재위치로 이동시켜주는 트래킹 모드 설정
        mapView.setTrackingMode(false);
        // 시야 표출 여부 설정정
        //mapView.setSightVisible(true);
        mapLayout.addView(mapView);
        centerPoint = new TMapMarkerItem();
        fromIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_startpoint);
        fromSetIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_startpoint_pin);
        toIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_destination);
        toSetIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_pin_destination);



        fromIcon = Bitmap.createScaledBitmap(fromIcon, 200, 200, true);
        fromSetIcon = Bitmap.createScaledBitmap(fromSetIcon, 150, 150, true);
        toIcon = Bitmap.createScaledBitmap(toIcon, 200, 200, true);
        toSetIcon = Bitmap.createScaledBitmap(toSetIcon, 150, 150, true);

        tMapData = new TMapData();
        mapView.addMarkerItem("center",centerPoint);
        centerPoint.setIcon(fromIcon);
        centerPoint.setTMapPoint(mapView.getCenterPoint());

        mapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                // 드래그 리스너
                //Log.d(TAG,"드래깅");
                centerPoint.setTMapPoint(mapView.getCenterPoint());
                tMapData.convertGpsToAddress(mapView.getLatitude(), mapView.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String s) {
                        address = s;
                    }
                });

                //Util.slideToBottom(applypoolBT);
                //applypoolBT.setVisibility(View.GONE);
                searchBarLayout.setVisibility(View.GONE);
                addressTV.setText("위치를 찾고 있습니다...");

            }
        });

        mapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                //Log.d(TAG,"드래깅 끝");
                //applypoolBT.setVisibility(View.VISIBLE);
                searchBarLayout.setVisibility(View.VISIBLE);
                if(address!=null){
                    addressTV.setText(address);
                    if(isStartEnabled) {
                        fromBT.setText(address);
                        guestRoute.setStartLati(tMapPoint.getLatitude());
                        guestRoute.setStartLongi(tMapPoint.getLongitude());
                    }else{
                        toBT.setText(address);
                        guestRoute.setEndLati(tMapPoint.getLatitude());
                        guestRoute.setEndLongi(tMapPoint.getLongitude());
                        if(guestRoute.getStartLati()!=0 && priceLayout.getVisibility() == View.VISIBLE){
                            guestRoute.setMoney(Util.calcMoney(guestRoute));
                            prictTV.setText(Util.formatMoney(guestRoute.getMoney()));
                        }
                    }

                }
            }
        });
        MapViewController.getInstance().showRoute(mapView,item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fromBT:
                isStartEnabled = true;
                fromTV.setTextColor(Color.RED);
                fromTV.setTypeface(null, Typeface.BOLD);
                fromBT.setTextColor(Color.RED);
                toBT.setTextColor(Color.BLACK);
                toTV.setTextColor(Color.BLACK);
                toTV.setTypeface(null, Typeface.NORMAL);

                centerPoint.setIcon(fromIcon);

                if(mapView.getMarkerItemFromID("fromset")!=null) {
                    mapView.removeMarkerItem("fromset");
                }

                if(guestRoute.getEndLati() != 0) {
                    TMapMarkerItem toSetMarker = new TMapMarkerItem();
                    toSetMarker.setTMapPoint(new TMapPoint(guestRoute.getEndLati(), guestRoute.getEndLongi()));
                    toSetMarker.setIcon(toSetIcon);
                    mapView.addMarkerItem("toset", toSetMarker);
                }

                break;

            case R.id.toBT:
                isStartEnabled = false;
                toTV.setTextColor(Color.RED);
                fromTV.setTypeface(null, Typeface.NORMAL);
                fromTV.setTextColor(Color.BLACK);
                toTV.setTypeface(null, Typeface.BOLD);
                fromBT.setTextColor(Color.BLACK);
                toBT.setTextColor(Color.RED);

                centerPoint.setIcon(toIcon);

                if(mapView.getMarkerItemFromID("toset")!=null)
                    mapView.removeMarkerItem("toset");
                if(guestRoute.getStartLati() != 0) {
                    TMapMarkerItem fromSetMarker = new TMapMarkerItem();
                    fromSetMarker.setTMapPoint(new TMapPoint(guestRoute.getStartLati(), guestRoute.getStartLongi()));
                    fromSetMarker.setIcon(fromSetIcon);
                    mapView.addMarkerItem("fromset", fromSetMarker);
                    priceLayout.setVisibility(View.VISIBLE);
                    applypoolBT.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.applypoolBT:
                if(DatabaseManager.getInstance().requestPool(item.getIdx(), LoginManager.getInstance().getUserData().getStNum(),guestRoute)){
                    boolean success =false;
                    String title = "카풀 요청 도착!";
                    String message = "카풀 요청이 도착했어요! \n 어플리케이션에서 확인해주세요~";
                    success = DatabaseManager.getInstance().requestPush(item.getId(),title,message,"d");
                    if(success)
                        Toast.makeText(getApplicationContext(),"신청 완료",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"신청 실패( 푸시 서버 에러)",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"신청 실패(서버 에러)",Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }
}
