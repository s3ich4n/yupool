package com.cs2017.yupool.Database.Threads;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.LoginManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class InitRun extends DBRun {
    private static final String TAG = "InitRun";

    @Override
    public void run() {
        try {
            UserData userData = LoginManager.getInstance().getUserData();
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"inituser.jsp")
                    .method(Connection.Method.POST)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .data("id", userData.getStNum())
                    .data("name",userData.getName())
                    .data("phone",userData.getPhone())
                    .execute();
            LoginManager.getInstance().uploadImageToServer();

            SharedPreferences pref = LoginManager.getInstance().getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String token = pref.getString("TOKEN","");
            if(token!=null && token.length()>0){
                Log.d(TAG,"토큰 존재!! :: "+ token);
                res = Jsoup.connect(TOMCAT_URL+"token.jsp")
                        .data("id",userData.getStNum())
                        .data("token",token)
                        .execute();
            }
            Log.d(TAG,"InitRun");
            Log.d(TAG,"서버 응답["+res.statusCode()+"] :" +res.body());
            checkSuccess(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
