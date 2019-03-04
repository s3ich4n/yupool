package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class StateRun extends DBRun {
    private static final String TAG = "StateRun";

    private String idx;
    private int state;
    public StateRun(String idx,int state){
        this.idx = idx;
        this.state = state;
    }
    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"state.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("idx", idx)
                    .data("state",String.valueOf(state))
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
