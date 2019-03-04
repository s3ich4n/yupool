package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.RoutePoint;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class RequestPoolRun extends DBRun {
    private static final String TAG = "RequestPoolRun";

    private String IDX;
    private String guestID;
    private RoutePoint guestRoute;
    public RequestPoolRun(String IDX, String guestID, RoutePoint routePoint){
        this.IDX = IDX;
        this.guestID = guestID;
        this.guestRoute = routePoint;
    }
    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"waiting.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("idx", IDX)
                    .data("guestid", guestID)
                    .data("slat", String.valueOf(guestRoute.getStartLati()))
                    .data("slon", String.valueOf(guestRoute.getStartLongi()))
                    .data("elat", String.valueOf(guestRoute.getEndLati()))
                    .data("elon", String.valueOf(guestRoute.getEndLongi()))
                    .data("price", String.valueOf(guestRoute.getMoney()))
                    .execute();
            Log.d(TAG,"서버 응답["+res.statusCode()+"] :" +res.body());
            checkSuccess(res);
            if(res.body().contains("true"))
                super.success = true;
            else
                super.success = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
