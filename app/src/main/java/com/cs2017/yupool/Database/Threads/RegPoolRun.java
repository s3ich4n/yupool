package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.PoolRegister.PoolItem;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class RegPoolRun extends DBRun {
    private static final String TAG = "DriverChkRun";

    private PoolItem item;
    public RegPoolRun(PoolItem item){
        this.item = item;
    }
    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"regpool.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("date", item.getDate())
                    .data("id", item.getId())
                    .data("cmt", item.getComment())
                    .data("sdate", item.getStartDate())
                    .data("fname", item.getFromName())
                    .data("flati", item.getFromLati())
                    .data("flongi", item.getFromLongi())
                    .data("faddr",item.getFromAddr())
                    .data("tname", item.getToName())
                    .data("tlati", item.getToLati())
                    .data("tlongi", item.getToLongi())
                    .data("taddr",item.getToAddr())
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
