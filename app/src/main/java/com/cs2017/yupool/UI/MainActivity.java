package com.cs2017.yupool.UI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.cs2017.yupool.AlarmDialog.AlarmDialog;
import com.cs2017.yupool.GPS.GpsTrack.GPSManager;
import com.cs2017.yupool.PoolList.PoolListFragment;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.FragmentUtil.FragmentManager;
import com.cs2017.yupool.GPS.GPSService;
import com.cs2017.yupool.GPS.Interface.GPSServiceCallback;
import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.PermissionCheck;
import com.cs2017.yupool.R;
import com.cs2017.yupool.DriverRegister.RegisterFragment;
import com.cs2017.yupool.PoolRegister.PoolRegisterFragment;
import com.cs2017.yupool.ReqRecv.ReceiveList.ReceiveListFragment;
import com.cs2017.yupool.ReqRecv.RequestList.RequestListFragment;


import java.io.IOException;
import java.util.ArrayList;

import static com.cs2017.yupool.DriverRegister.RegisterFragment.REQ_CODE_SELECT_IMAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "MainActivity";
    private static int LOGIN_REQUEST = 500;

    // 프래그먼트들 선언
    PoolListFragment poolListFragment;
    RegisterFragment registerFragment;
    PoolRegisterFragment poolRegisterFragment;
    RequestListFragment requestListFragment;
    ReceiveListFragment receiveListFragment;

    // 네비게이션 뷰
    NavigationView navigationView;

    //  GPS 서비스
    private GPSService gpsService;
    private ServiceConnection serviceConnection; // GPS 서비스와의 연결
    private boolean isBounded = false; // GPS 서비스가 연결되어있는지 여부


    // 액티비티 최초 실행시 호출
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initDrawer();
        initFragments();
        // 로그인이 완료되어 있지 않은 경우
        if(!LoginManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }else{
            // 로그인이 이미 되어있을 경우 사용자 네베게이션 뷰를 초기화
            initUserNavView();
        }

        checkPermission();
        Log.d(TAG,"OnCreate");


        // Firebase Cloud Messaging
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
    }

/////////////////////////////////GPS 서비스 관련   //////////////////////////////////////////////////

    // 현재 위치 변화를 감지하여 지도에 표시한다.
    private GPSServiceCallback gpsServiceCallback = new GPSServiceCallback() {
        @Override
        public void onChangeCurrentLocation(Location location) {
            System.out.println("onChangeCurrentLocation");
            poolListFragment.setUserLocation(location);
            poolRegisterFragment.setUserLocation(location);
            //poolListFragment.setMapCenter(location);
        }
    };

    // 서비스를 초기화 하는 메소드
    private void initializeService(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                GPSService.GPSBinder binder = (GPSService.GPSBinder) iBinder;
                gpsService = binder.getService();
                gpsService.setCallback(gpsServiceCallback);
                isBounded = true;
                GPSManager.getInstance().setGpsService(gpsService);
                Log.d("쿠키테스트","서비스 연결");
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBounded = false;
            }
        };
        Intent i = new Intent(MainActivity.this,GPSService.class);
        bindService(i,serviceConnection,BIND_AUTO_CREATE);
    }


/////////////////////////////////GPS 서비스 관련  끝//////////////////////////////////////////////////

    /*
      권한 체크
  */
    private void checkPermission(){
        ArrayList<String> permissions= new ArrayList<String>();
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        PermissionCheck.getInstance().setFailMessage("어플리케이션 실행을 위해서는 권한 승인이 필요합니다");
        PermissionCheck.getInstance().checkPermission(this,permissions.toArray(new String[permissions.size()]));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // To re-request the permissions if deneid
        PermissionCheck.getInstance().resultOfRequest(requestCode,permissions,grantResults);
        initializeService();
    }

    // 로그인 성공시 호출된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG,"onActivityResult ::"+ resultCode);
        if(requestCode == LOGIN_REQUEST && resultCode == RESULT_OK){
            // 로그인 성공 시
            initUserNavView();
            DatabaseManager.getInstance().initUserData();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    // 좌측 네비게이션 뷰를 초기화하는 함수
    private void initUserNavView(){
        Log.d(TAG,"initUserNavView");
        View navHeaderView = navigationView.getHeaderView(0);
        UserData userData = LoginManager.getInstance().getUserData();

        TextView nameTV = navHeaderView.findViewById(R.id.nameTV);
        TextView phoneTV = navHeaderView.findViewById(R.id.phoneTV);
        TextView roleTV = navHeaderView.findViewById(R.id.roleTV);
        ImageView profileIV = navHeaderView.findViewById(R.id.profileIV);
        nameTV.setText(userData.getName());
        phoneTV.setText(userData.getPhone());
        if(userData.isDriver()) {
            if(DatabaseManager.getInstance().isApproved()){
                roleTV.setText("드라이버");
                FragmentManager.getInstance().replaceFragment(poolRegisterFragment);
            }else{
                roleTV.setTextColor(Color.RED);
                roleTV.setText("미승인된 드라이버");
                AlarmDialog.getInstance().show(MainActivity.this,"드라이버로 승인되어있지 않습니다","드라이버 등록 후 이용해 주세요");
                FragmentManager.getInstance().replaceFragment(registerFragment);
            }
        }
        else {
            roleTV.setText("게스트");
        }
        profileIV.setImageBitmap(userData.getImage());
    }

    // 프레그먼트들을 초기화
    private void initFragments(){
        Log.d(TAG,"initFragments");
        poolListFragment = new PoolListFragment();
        registerFragment = new RegisterFragment();
        poolRegisterFragment = new PoolRegisterFragment();
        requestListFragment = new RequestListFragment();
        receiveListFragment = new ReceiveListFragment();

        FragmentManager.getInstance().registerContext(MainActivity.this);
        FragmentManager.getInstance().setDriverFragment(poolRegisterFragment);
        FragmentManager.getInstance().setReceiveListFragment(receiveListFragment);
        FragmentManager.getInstance().setRequestListFragment(requestListFragment);

        if(getIntent().getExtras() == null) {
            FragmentManager.getInstance().replaceFragment(poolListFragment);
            Log.d(TAG,"기본 내역");
        }
        else {
            if(getIntent().getExtras()!=null) {
                String type = getIntent().getExtras().getString("type");
                if(type !=null) {
                    if (type.equals("g")) {
                        FragmentManager.getInstance().chageToRequestFragment();
                        Log.d(TAG, "보낸 요청 내역");
                    } else if (type.equals("d")) {
                        FragmentManager.getInstance().changeToReceiveFragment();
                        Log.d(TAG, "받은 요청 내역");
                    } else if (type.equals("f")) {
                        //FragmentManager.getInstance().replaceFragment(poolListFragment);
                    }
                }
            }
        }
    }

    // 좌측 네비게이션 drawer를 초기화
    private void initDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Drawer 의 아이콘 흑백 방지를 위함함
        navigationView.setItemIconTintList(null);
    }

    // 좌측 네비게이션 drawer 의 온 클릭 리스너
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_poolList) {
            FragmentManager.getInstance().replaceFragment(poolListFragment);
        } else if (id == R.id.nav_register) {
            FragmentManager.getInstance().replaceFragment(registerFragment);
        } else if( id == R.id.nav_regPool){
            FragmentManager.getInstance().replaceFragment(poolRegisterFragment);
        } else if (id == R.id.nav_request){
            FragmentManager.getInstance().replaceFragment(requestListFragment);
        } else if( id == R.id.nav_receive){
            FragmentManager.getInstance().replaceFragment(receiveListFragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        // 백키 대신 홈키로 처리한다
        if(FragmentManager.getInstance().getCurrentFragment() == FragmentManager.REG_DETAIL_FRAGMENT){
            FragmentManager.getInstance().replaceFragment(poolRegisterFragment);
        }else {
            if (poolListFragment.isSearchLayoutVisible()) {
                poolListFragment.setSearchLayoutVisible(false);
            } else if(FragmentManager.getInstance().getCurrentFragment() == FragmentManager.REG_POOL_FRAGMENT){
                //
            }
            else {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume()");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }

    public GPSService getGpsServiceRef(){
        return gpsService;
    }
}
