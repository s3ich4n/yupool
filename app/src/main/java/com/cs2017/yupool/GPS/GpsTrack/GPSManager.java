package com.cs2017.yupool.GPS.GpsTrack;

import android.location.Location;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.GPS.GPSService;
import com.cs2017.yupool.GPS.Interface.GPSReadCallback;
import com.cs2017.yupool.GPS.Interface.GPSServiceCallback;
import com.cs2017.yupool.RoutePoint;

import java.util.Map;
import java.util.Timer;

/**
 * Created by cs2017 on 2017-12-02.
 */

public class GPSManager {

    private Map<String,String> cookie;
    private static GPSManager instance = new GPSManager();
    private GPSService gpsService;
    private Timer timer;
    private ReadGPSTask readGPSTask;


    private GPSManager(){};
    public static GPSManager getInstance(){
        return instance;
    }


    public void setGpsService(GPSService gpsService){
        this.gpsService = gpsService;

        this.cookie = DatabaseManager.getInstance().getCookie();
        gpsService.setCookie(cookie);
    }

    public void startWrite(String IDX, RoutePoint routePoint,String guestID){
        gpsService.setIdx(IDX);
        gpsService.startWrite(routePoint,guestID);
    }

    public void stopWrite(){
        gpsService.stopWrite();
    }

    public void setCallback(GPSServiceCallback gpsServiceCallback){
        gpsService.setCallback(gpsServiceCallback);
    }


    public void startRead(String IDX, int period,GPSReadCallback gpsReadCallback){
        readGPSTask = new ReadGPSTask(IDX,cookie,gpsReadCallback);
        timer = new Timer();
        timer.schedule(readGPSTask,0,period);
    }

    public Location getLastLocation(){
        return gpsService.getLastLocation();
    }

    public void stopRead(){
        if(timer!=null && readGPSTask !=null) {
            readGPSTask.cancel();
            timer.cancel();
        }

    }


}
