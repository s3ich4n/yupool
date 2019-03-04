package com.cs2017.yupool.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.GPS.GpsTrack.GPSManager;
import com.cs2017.yupool.GPS.Interface.GPSReadCallback;
import com.cs2017.yupool.GPS.Interface.GPSServiceCallback;
import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.R;
import com.cs2017.yupool.ReqRecv.Adapter.GetAddressTask;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;
import com.cs2017.yupool.StaticVal;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class GuestDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private WaitingItem item;
    private LinearLayout BTLayout;
    private Button denyBT;
    private ImageView image;
    private ImageView carImage;
    private TextView commentTV;
    private TextView timeTV;
    private Button currentBT;
    private LinearLayout topLayout;

    private RelativeLayout mapLayout;
    private TMapView mapView;
    private Bitmap toIcon;
    private Bitmap fromIcon;
    private Bitmap startIcon;
    private Bitmap endIcon;

    private TMapMarkerItem startMarker;
    private TMapMarkerItem endMarker;

    private int state;

    private FrameLayout BTLayout2;
    private Button callBT;
    private String phone;
    private Button kakaoBT;


    private TMapMarkerItem myMarker;
    private Bitmap myIcon;

    private TMapMarkerItem targetMarker;
    private Bitmap targetIcon;

    private  TMapPoint targetLocation;
    private  Location lastLocation;
    private Button targetBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_detail);

        //item = (WaitingItem) getIntent().getSerializableExtra("item");
        item = DataManager.getInstance().getItem();
        state = item.getRunFlag();
        getSupportActionBar().setTitle("요청한 카풀");
        initViews();
        initMapView();

        if(state == StaticVal.DRIVING){
            mapView.removeAllMarkerItem();
            mapView.removeAllTMapPolyLine();
            myMarker = new TMapMarkerItem();
            myIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_location);
            myIcon = Bitmap.createScaledBitmap(myIcon, 200, 200, true);
            myMarker.setIcon(myIcon);

            mapView.addMarkerItem("my",myMarker);
            lastLocation = GPSManager.getInstance().getLastLocation();
            if(lastLocation!=null)
                myMarker.setTMapPoint(new TMapPoint(lastLocation.getLatitude(),lastLocation.getLongitude()));
            // 자신 위치 표시
            GPSManager.getInstance().setCallback(new GPSServiceCallback() {
                @Override
                public void onChangeCurrentLocation(android.location.Location location) {
                        lastLocation = location;
                        myMarker.setTMapPoint(new TMapPoint(location.getLatitude(),location.getLongitude()));
                        mapView.addMarkerItem("my",myMarker);
                }
            });


            targetMarker = new TMapMarkerItem();
            targetIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.car);
            targetIcon = Bitmap.createScaledBitmap(targetIcon, 200, 200, true);
            targetMarker.setIcon(targetIcon);
            mapView.addMarkerItem("target",targetMarker);

            // 상대 위치 표시
            GPSManager.getInstance().startRead(item.getPoolItem().getIdx(), 1000, new GPSReadCallback() {
                @Override
                public void onRead(double latitude, double longitude) {
                    if(targetLocation!=null) {
                        targetLocation.setLatitude(longitude);
                        targetLocation.setLongitude(latitude);
                    }else{
                        targetLocation = new TMapPoint(longitude, latitude);
                    }
                    targetMarker.setTMapPoint(new TMapPoint(longitude,latitude));
                    mapView.addMarkerItem("target",targetMarker);
                }
            });
        }
    }

    private void initViews(){
        BTLayout = findViewById(R.id.BTLayout);
        denyBT = findViewById(R.id.denyBT);
        image = findViewById(R.id.image);
        denyBT.setOnClickListener(this);
        carImage = findViewById(R.id.carimage);
        commentTV = findViewById(R.id.commentTV);
        timeTV = findViewById(R.id.timeTV);
        currentBT = findViewById(R.id.currentBT);
        currentBT.setOnClickListener(this);
        topLayout = findViewById(R.id.topLayout);
        targetBT = findViewById(R.id.targetBT);
        targetBT.setOnClickListener(this);

        BTLayout2 = findViewById(R.id.BTLayout2);
        callBT= findViewById(R.id.callBT);
        callBT.setOnClickListener(this);
        kakaoBT = findViewById(R.id.kakaoBT);
        kakaoBT.setOnClickListener(this);

        timeTV.setText(item.getPoolItem().getDate());
        commentTV.setText(item.getPoolItem().getComment());
        Glide.with(getApplicationContext()).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getPoolItem().getId()+".jpg")
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(image);
        Glide.with(getApplicationContext()).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getPoolItem().getId()+"_car.jpg")
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(carImage );


        if(state == StaticVal.WAITING_ACCEPT){
            BTLayout2.setVisibility(View.GONE);
        }else if(state == StaticVal.WAITING_DRIVE){
            initPhone();
            denyBT.setText("드라이버의 운행시작을 기다려주세요");
            denyBT.setEnabled(false);
            BTLayout2.setVisibility(View.VISIBLE);
            currentBT.setVisibility(View.GONE);
            targetBT.setVisibility(View.GONE);
            initPhone();
        }else if(state == StaticVal.DRIVING){
            initPhone();
            denyBT.setEnabled(false);
            denyBT.setText("드라이버가 출발하였습니다");
            currentBT.setVisibility(View.VISIBLE);
            targetBT.setVisibility(View.VISIBLE);
            BTLayout2.setVisibility(View.VISIBLE);
        }

    }

    private void initMapView(){
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

        fromIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_startpoint);
        toIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon_destination);

        startIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.poi_departure_with_shadow_small);
        endIcon =  BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.poi_destination_with_shadow_small);

        fromIcon = Bitmap.createScaledBitmap(fromIcon, 200, 200, true);
        toIcon = Bitmap.createScaledBitmap(toIcon, 200, 200, true);

        startIcon = Bitmap.createScaledBitmap(startIcon, 100, 150, true);
        endIcon = Bitmap.createScaledBitmap(endIcon, 100, 150, true);

        startMarker = new TMapMarkerItem();
        startMarker.setIcon(startIcon);
        startMarker.setTMapPoint(new TMapPoint(item.getRoutePoint().getStartLati(),item.getRoutePoint().getStartLongi()));
        startMarker.setAutoCalloutVisible(true);
        startMarker.setCanShowCallout(true);
        startMarker.setCalloutTitle("탑승지");
        mapView.addMarkerItem("start",startMarker);

        endMarker = new TMapMarkerItem();
        endMarker.setIcon(endIcon);
        endMarker.setTMapPoint(new TMapPoint(item.getRoutePoint().getEndLati(),item.getRoutePoint().getEndLongi()));
        endMarker.setAutoCalloutVisible(true);
        endMarker.setCanShowCallout(true);
        endMarker.setCalloutTitle("하차지");
        mapView.addMarkerItem("end",endMarker);

        mapView.setCenterPoint(item.getRoutePoint().getStartLongi(),item.getRoutePoint().getStartLati(),true);
        mapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                // 드래그 리스너BTLayout.setVisibility(View.GONE);
                topLayout.setVisibility(View.GONE);
            }
        });

        mapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                topLayout.setVisibility(View.VISIBLE);
            }
        });
        MapViewController.getInstance().showRoute(mapView,item.getPoolItem());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.denyBT:
                if(DatabaseManager.getInstance().cancelPool(item.getPoolItem().getIdx())) {
                    Toast.makeText(getApplicationContext(), "카풀 요청을 취소하였습니다", Toast.LENGTH_SHORT).show();
                    DatabaseManager.getInstance().requestPush(item.getGuestID(),"카풀신청이 취소되었습니다","아쉽게도 게스트가 카풀신청을 취소했어요ㅠㅠ","d");
                }
                else
                    Toast.makeText(getApplicationContext(),"서버 통신 에러",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.callBT:
                if(phone!=null){
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(phone)));
                }else
                    Toast.makeText(getApplicationContext(),"핸드폰번호 null",Toast.LENGTH_SHORT).show();
                break;
            case R.id.kakaoBT:
                try {
                    KakaoTalkLinkMessageBuilder builder = KakaoLink.getKakaoLink(getApplicationContext()).createKakaoTalkLinkMessageBuilder();
                    builder.addImage("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getPoolItem().getId()+".jpg",200,250);
                    builder.addText("곧 출발해요~ \n [요금]:"+(int)item.getRoutePoint().getMoney()+"원"+"\n [출발지]: "+item.getPoolItem().getFromName()+"\n [도착지]: "+
                    item.getPoolItem().getToName());
                    KakaoLink.getKakaoLink(getApplicationContext()).sendMessage(builder,getApplicationContext());
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.currentBT:
                if(lastLocation!=null)
                    mapView.setCenterPoint(lastLocation.getLongitude(),lastLocation.getLatitude(),true);
                break;
            case R.id.targetBT:
                if(targetLocation!=null)
                    mapView.setCenterPoint(targetLocation.getLongitude(),targetLocation.getLatitude(),true);
                break;
        }
    }

    private void initPhone(){
        phone = DatabaseManager.getInstance().getPhoneNum(item.getPoolItem().getId());
        if(phone ==null)
            Toast.makeText(getApplicationContext(),"핸드폰번호 받아오기 실패!",Toast.LENGTH_SHORT).show();
        else
            phone = "tel:"+phone;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GPSManager.getInstance().stopRead();
    }
}
