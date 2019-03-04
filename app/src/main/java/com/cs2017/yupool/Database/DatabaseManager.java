package com.cs2017.yupool.Database;

import com.cs2017.yupool.Database.Threads.CancelRun;
import com.cs2017.yupool.Database.Threads.DriverChkRun;
import com.cs2017.yupool.Database.Threads.GetPhoneRun;
import com.cs2017.yupool.Database.Threads.GetPoolRun;
import com.cs2017.yupool.Database.Threads.GetWaitListRun;
import com.cs2017.yupool.Database.Threads.InitRun;
import com.cs2017.yupool.Database.Threads.RegDriverRun;
import com.cs2017.yupool.Database.Threads.RegPoolRun;
import com.cs2017.yupool.Database.Threads.RequestPoolRun;
import com.cs2017.yupool.Database.Threads.RequestPushRun;
import com.cs2017.yupool.Database.Threads.StateRun;
import com.cs2017.yupool.GPS.GpsTrack.SessionTask;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;
import com.cs2017.yupool.RoutePoint;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class DatabaseManager {

    private Map<String,String> cookie;
    private static DatabaseManager instance = new DatabaseManager();
    private DatabaseManager(){
        SessionTask sessionTask = new SessionTask();
        sessionTask.start();
        try {
            sessionTask.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.cookie = sessionTask.getCookie();
    };

    public static DatabaseManager getInstance(){
        return instance;
    }

    // 사용자 정보 id, name, phoneNumber 를 서버 데이터베이스에 초기화
    public boolean initUserData(){
        InitRun initRun = new InitRun();
        Thread t = new Thread(initRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(initRun.isSuccess())
            return true;
        else
            return false;
    }
    // 사용자가 드라이버 승인된 사용자인지 조회
    public boolean isApproved(){
        DriverChkRun driverChkRun = new DriverChkRun();
        Thread t = new Thread(driverChkRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(driverChkRun.isSuccess() && driverChkRun.isDriver())
            return true;
        else
            return false;
    }

    // 면허증 진위여부 조회 후, 데이터베이스에 면허 승인 true 로 바꿈
    public boolean setApproved(){
        RegDriverRun regDriverRun = new RegDriverRun();
        Thread t = new Thread(regDriverRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(regDriverRun.isSuccess())
            return true;
        else
            return false;
    }

    // 카풀 등록 쓰레드
    public boolean registerPool(PoolItem item){
        RegPoolRun regPoolRun = new RegPoolRun(item);
        Thread t = new Thread(regPoolRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(regPoolRun.isSuccess())
            return true;
        else
            return false;
    }

    public ArrayList<PoolItem> getPoolList(String id){
        GetPoolRun getPoolRun = new GetPoolRun(id);
        Thread t = new Thread(getPoolRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(getPoolRun.isSuccess())
            return getPoolRun.getList();
        else
            return null;
    }


    public ArrayList<WaitingItem> getWaitList(String id,int type){
        GetWaitListRun getWaitListRun = new GetWaitListRun(id,type);
        Thread t = new Thread(getWaitListRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(getWaitListRun.isSuccess())
            return getWaitListRun.getList();
        else
            return null;
    }
    public boolean requestPool(String IDX, String guestID, RoutePoint routePoint){
        RequestPoolRun requestPoolRun = new RequestPoolRun(IDX,guestID,routePoint);
        Thread t = new Thread(requestPoolRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(requestPoolRun.isSuccess())
            return true;
        else
            return false;
    }

    public String getPhoneNum(String id){
        GetPhoneRun getPhoneRun = new GetPhoneRun(id);
        Thread t = new Thread(getPhoneRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(getPhoneRun.isSuccess())
            return getPhoneRun.getPhone();
        else
            return null;

    }

    public boolean cancelPool(String IDX){
        CancelRun cancelRun = new CancelRun(IDX);
        Thread t = new Thread(cancelRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(cancelRun.isSuccess())
            return true;
        else
            return false;
    }
    public boolean changeState(String IDX,int state){
        StateRun stateRun = new StateRun(IDX,state);
        Thread t = new Thread(stateRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(stateRun.isSuccess())
            return true;
        else
            return false;
    }

    public boolean requestPush(String receiver, String title, String message,String type){
        RequestPushRun requestPushRun = new RequestPushRun(receiver,title,message,type);
        Thread t = new Thread(requestPushRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(requestPushRun.isSuccess())
            return true;
        else
            return false;
    }


    public Map<String,String> getCookie(){
        return cookie;
    }

}
