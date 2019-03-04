package com.cs2017.yupool.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.cs2017.yupool.AlarmDialog.AlarmDialog;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.GPS.GpsTrack.GPSManager;
import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.R;
import com.cs2017.yupool.ReqRecv.Adapter.GetAddressTask;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;
import com.cs2017.yupool.StaticVal;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DriverDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private WaitingItem item;
    private LinearLayout BTLayout;
    private Button acceptBT;
    private Button denyBT;
    private ImageView image;
    private TextView fromBT;
    private TextView toBT;

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
    private Button runBT;
    private Button callBT;
    private String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);

        //item = (WaitingItem) getIntent().getSerializableExtra("item");
        item = DataManager.getInstance().getItem();
        state = item.getRunFlag();
        Log.d("EEEEE","스테이트 = "+state);
        getSupportActionBar().setTitle("요청받은 카풀");
        initViews();
        initMapView();


    }

    private void initViews(){
        BTLayout = findViewById(R.id.BTLayout);
        acceptBT = findViewById(R.id.acceptBT);
        denyBT = findViewById(R.id.denyBT);
        image = findViewById(R.id.image);
        fromBT = findViewById(R.id.fromBT);
        toBT = findViewById(R.id.toBT);

        acceptBT.setOnClickListener(this);
        denyBT.setOnClickListener(this);
        fromBT.setOnClickListener(this);
        toBT.setOnClickListener(this);


        BTLayout2 = findViewById(R.id.BTLayout2);
        runBT = findViewById(R.id.runBT);
        callBT= findViewById(R.id.callBT);
        runBT.setOnClickListener(this);
        callBT.setOnClickListener(this);

        GetAddressTask getAddressTask1 = new GetAddressTask(fromBT,item.getRoutePoint().getStartLati(),item.getRoutePoint().getStartLongi());
        GetAddressTask getAddressTask2 = new GetAddressTask(toBT,item.getRoutePoint().getEndLati(),item.getRoutePoint().getEndLongi());

        getAddressTask1.execute();
        getAddressTask2.execute();
        Glide.with(getApplicationContext()).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getGuestID()+".jpg")
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(image);

        if(state == StaticVal.WAITING_ACCEPT){
            BTLayout.setVisibility(View.VISIBLE);
            BTLayout2.setVisibility(View.GONE);
        }else if(state == StaticVal.WAITING_DRIVE){
            BTLayout.setVisibility(View.GONE);
            BTLayout2.setVisibility(View.VISIBLE);
            initPhone();
        }else if(state == StaticVal.DRIVING){
            runBT.setText("운행 종료");
            runBT.setBackgroundColor(Color.RED);
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
                if(state == StaticVal.WAITING_ACCEPT)
                    BTLayout.setVisibility(View.GONE);
            }
        });

        mapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                if(state == StaticVal.WAITING_ACCEPT)
                    BTLayout.setVisibility(View.VISIBLE);
            }
        });
        MapViewController.getInstance().showRoute(mapView,item.getPoolItem());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.acceptBT:
                if(DatabaseManager.getInstance().changeState(item.getPoolItem().getIdx(), StaticVal.WAITING_DRIVE)) {
                    DatabaseManager.getInstance().requestPush(item.getGuestID(),"카풀신청이 수락되었습니다!","어플리케이션에서 확인해주세요~","g");
                    Toast.makeText(getApplicationContext(), "카풀 요청을 수락하였습니다!", Toast.LENGTH_SHORT).show();
                    state = StaticVal.WAITING_DRIVE;
                    DataManager.getInstance().getItem().setRunFlag(StaticVal.WAITING_DRIVE);
                    BTLayout.setVisibility(View.GONE);
                    BTLayout2.setVisibility(View.VISIBLE);
                    // 핸드폰 번호 받아와야함
                    initPhone();
                }
                else
                    Toast.makeText(getApplicationContext(),"서버 통신 에러",Toast.LENGTH_SHORT).show();
                break;
            case R.id.denyBT:
                if(DatabaseManager.getInstance().cancelPool(item.getPoolItem().getIdx())) {
                    Toast.makeText(getApplicationContext(), "카풀 요청이 거절되었습니다", Toast.LENGTH_SHORT).show();
                    DatabaseManager.getInstance().requestPush(item.getGuestID(),"카풀신청이 거절되었습니다","아쉽게도 드라이버가 카풀신청을 거절했어요ㅠㅠ","g");
                }
                else
                    Toast.makeText(getApplicationContext(),"서버 통신 에러",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.fromBT:
                mapView.setCenterPoint(item.getRoutePoint().getStartLongi(),item.getRoutePoint().getStartLati(),true);
                break;
            case R.id.toBT:
                mapView.setCenterPoint(item.getRoutePoint().getEndLongi(),item.getRoutePoint().getEndLati(),true);
                break;

            case R.id.runBT:
                // 카카오 네비 시작
                if(state == StaticVal.WAITING_DRIVE) {
                    if (DatabaseManager.getInstance().changeState(item.getPoolItem().getIdx(), StaticVal.DRIVING)) {
                        DatabaseManager.getInstance().requestPush(item.getGuestID(), "드라이버가 출발 알림", "드라이버가 운행을 시작하였습니다", "g");
                        state = StaticVal.DRIVING;
                        DataManager.getInstance().getItem().setRunFlag(StaticVal.DRIVING);
                        runBT.setText("운행 종료");
                        runBT.setBackgroundColor(Color.RED);
                        Location destination = Location.newBuilder("내 목적지", Double.valueOf(item.getPoolItem().getToLongi()), Double.valueOf(item.getPoolItem().getToLati())).build();
                        NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();
                        List<Location> viaList = new ArrayList<Location>();
                        viaList.add(Location.newBuilder("게스트 탑승위치", item.getRoutePoint().getStartLongi(), item.getRoutePoint().getStartLati()).build());
                        viaList.add(Location.newBuilder("게스트 하차위치", item.getRoutePoint().getEndLongi(), item.getRoutePoint().getEndLati()).build());
                        KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options).setViaList(viaList);
                        KakaoNaviService.navigate(this, builder.build());

                        // GPS 쓰기시작
                        GPSManager.getInstance().startWrite(item.getPoolItem().getIdx(),item.getRoutePoint(),item.getGuestID());

                        // 핸드폰 번호 받아와야함
                        initPhone();
                    } else {
                            Toast.makeText(getApplicationContext(),"서버 통신 실패",Toast.LENGTH_SHORT).show();
                    }
                }else if(state == StaticVal.DRIVING){
                    // 바루지워버림
                    if(DatabaseManager.getInstance().cancelPool(item.getPoolItem().getIdx())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("운행이 종료되었습니다");
                        builder.setMessage("이용해 주셔서 감사합니다");
                        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();

                        DatabaseManager.getInstance().requestPush(item.getGuestID(),"운행이 종료되었습니다","드라이버를 평가해 주세요!","f");

                        // gps 종료
                        GPSManager.getInstance().stopWrite();
                    }else{
                        Toast.makeText(getApplicationContext(), "서버 통신 에러", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.callBT:
                if(phone!=null){
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(phone)));
                }else
                    Toast.makeText(getApplicationContext(),"핸드폰번호 null",Toast.LENGTH_SHORT).show();
                // 전화 걸기
                break;
        }
    }

    private void initPhone(){
        phone = DatabaseManager.getInstance().getPhoneNum(item.getGuestID());
        if(phone ==null)
            Toast.makeText(getApplicationContext(),"핸드폰번호 받아오기 실패!",Toast.LENGTH_SHORT).show();
        else
            phone = "tel:"+phone;
    }
}
