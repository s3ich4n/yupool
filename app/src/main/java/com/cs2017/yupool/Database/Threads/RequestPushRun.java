package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.RoutePoint;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class RequestPushRun extends DBRun {
    private static final String TAG = "RequestPushRun";

    private String receiver;
    private String title;
    private String message;
    private String type;
    public RequestPushRun(String receiver, String title, String message,String type){
        this.receiver = receiver;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"message.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("id", receiver)
                    .data("msg", message)
                    .data("title", title)
                    .data("type",type)
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
