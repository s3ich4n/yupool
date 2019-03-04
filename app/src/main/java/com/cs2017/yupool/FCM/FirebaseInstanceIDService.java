package com.cs2017.yupool.FCM;

/**
 * Created by cs2017 on 2017-11-27.
 */

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.cs2017.yupool.Login.LoginManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FBInstanceIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences("pref",MODE_PRIVATE)
                .edit();
        editor.putString("TOKEN",token);
        editor.commit();
    }

    public void registerToken(){

    }
}
