package com.cs2017.yupool.GPS;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.GPS.GpsTrack.WriteGPSTask;
import com.cs2017.yupool.GPS.Interface.GPSServiceCallback;
import com.cs2017.yupool.RoutePoint;
import com.cs2017.yupool.Util;

import java.util.Map;

public class GPSService extends Service {

    private final static String TAG = "GPSService";
    private final IBinder gpsBinder = new GPSBinder();
    private GPSServiceCallback gpsServiceCallback;
    private GPSServiceCallback gpsServiceCallback2;

    private Map<String,String> cookie;
    private String idx;
    private boolean start = false;
    private Location lastLocation;

    private RoutePoint destination;
    private boolean send = false;
    private String guestID;


    // 사용자 위치 수신기
    private LocationManager locationManager;
    private LocationListener locationListener;
    // 위치정보 갱신 시간
    private int refreshTime = 1000;

    // 서비스 최초 생성시 실행, Location Manager 초기화
    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate()");
        super.onCreate();
    }
    // 컴포넌트에 반환해 줄 IBinder를 위한 클래스
    public class GPSBinder extends Binder {
        public GPSService getService() {
            return GPSService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind()");
        return gpsBinder;
    }

    // MainActivity 에서 GPS Service 에 콜백을 등록
    // 콜백 등록 후 init Cureent location chage listener
    public void setCallback(GPSServiceCallback gpsServiceCallback){
        this.gpsServiceCallback = gpsServiceCallback;
        initCurrentLocationChangeListener();
    }

    public void setCallback2(GPSServiceCallback gpsServiceCallback){
        this.gpsServiceCallback2 = gpsServiceCallback;
    }

    // 현재 위치 변화를 감지하는 location change listener 를 초기화
    @SuppressLint("MissingPermission")
    public void initCurrentLocationChangeListener(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //getCureentLocation();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                Log.d(TAG,"[현재위치] 위도 : " +lati + " 경도 : " + longi);
                gpsServiceCallback.onChangeCurrentLocation(location);
                if(gpsServiceCallback2 !=null)
                    gpsServiceCallback2.onChangeCurrentLocation(location);
                lastLocation = location;
                if(start){
                    WriteGPSTask writeGPSTask = new WriteGPSTask(idx,location.getLongitude(),location.getLatitude(), cookie);
                    writeGPSTask.execute();
                    Log.d(TAG,"[GPS Write] "+destination +" ,"+send + " , " +guestID);
                    if(destination!=null && send == false && guestID !=null){
                        if(Util.calcDistance(destination.getStartLati(),destination.getStartLongi(),location.getLatitude(),location.getLongitude())<=500) {
                            DatabaseManager.getInstance().requestPush(guestID,"드라이버가 곧 도착합니다","드라이버가 500미터 내에 도착하였습니다","g");
                            send = true;
                        }
                    }

                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.refreshTime, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.refreshTime, 0, locationListener);
    }


    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void startWrite(RoutePoint routePoint,String guestID){
        start = true;
        destination = routePoint;
        this.guestID = guestID;
        send = false;
        if(lastLocation!=null){
            // GPs writing
            WriteGPSTask writeGPSTask = new WriteGPSTask(idx,lastLocation.getLongitude(),lastLocation.getLatitude(), cookie);
            writeGPSTask.execute();
        }
    }
    public void stopWrite(){
        start =false;
        send = false;
    }


    // 수동으로 현재 위치를 가져오기 위한 함수
    @SuppressLint("MissingPermission")
    public Location getCureentLocation(){
        Location currentLocation = null;
        String locationProvider = LocationManager.GPS_PROVIDER;

        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        if(currentLocation!=null){
            double lati = currentLocation.getLatitude();
            double longi = currentLocation.getLongitude();
            Log.d(TAG,"위도 : " +lati + " 경도 : " + longi);
        }else{
            locationProvider = LocationManager.NETWORK_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if(currentLocation!=null){
                double lati = currentLocation.getLatitude();
                double longi = currentLocation.getLongitude();
                Log.d(TAG,"위도 : " +lati + " 경도 : " + longi);
            }else{
                Log.e(TAG,"Can't Get Current Location");
            }
        }
        return currentLocation;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind()");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }

    public Location getLastLocation(){
        return lastLocation;
    }
}
