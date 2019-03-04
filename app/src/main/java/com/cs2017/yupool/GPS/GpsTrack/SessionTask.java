package com.cs2017.yupool.GPS.GpsTrack;

import android.util.Log;

import com.cs2017.yupool.StaticVal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-10-03.
 */

public class SessionTask extends Thread {
    private static final String TAG = "SessionTask";

    private Map<String,String> cookie = null;

    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"gpsr.jsp")
                    .method(Connection.Method.GET)
                    .data("id","999")
                    .execute();
            cookie = res.cookies();
            Log.d(TAG,"세션얻어옴");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> getCookie(){
        return cookie;
    }


}
