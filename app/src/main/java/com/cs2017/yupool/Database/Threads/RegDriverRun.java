package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.LoginManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class RegDriverRun extends DBRun {
    private static final String TAG = "DriverChkRun";
    @Override
    public void run() {
        try {
            UserData userData = LoginManager.getInstance().getUserData();
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"regdriver.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("id", userData.getStNum())
                    .execute();
            Log.d(TAG,"서버 응답["+res.statusCode()+"] :" +res.body());
            checkSuccess(res);
            if(res.body().contains("success"))
                super.success = true;
            else
                super.success = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
