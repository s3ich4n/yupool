package com.cs2017.yupool.Login;

import android.content.Context;
import android.content.SharedPreferences;

import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.Interface.OnLoginFinishListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    어플리케이션에서 영남대학교 로그인 을 총괄하는 Login Manager
 */
public class LoginManager {
    //
    private static LoginManager instance = new LoginManager(); // 인스턴스
    private LoginManager(){}; // 생성자
    private UserData userData; // 사용자 데이터

    private SharedPreferences pref; // 시스템 내부 SharedPreference 조회를 위한 변수
    private  SharedPreferences.Editor editor; // 시스템 내부 SharedPreference 수정을 위한 변수
    private Context context; // 어플리케이션 컨텍스트

    private boolean loggedIn = false;


    // 로그인 메니저 내부 변수들을 초기화하는 함수
    public void initLoginManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences("pref",MODE_PRIVATE);
        editor = pref.edit();
    }

    // 로그인을 수행하는 함수
    // 아이디, 비밀번호, 자동 로그인 지정여부 , 콜벡 메소드
    public void login(String id, String pw, boolean autoLogin,boolean isDriver, OnLoginFinishListener onLoginFinishListener){
        LoginTask loginTask = new LoginTask(id,pw,autoLogin,editor,isDriver,onLoginFinishListener,context);
        loginTask.execute();
    }


    // LoginManager 의 인스턴스를 반환하는 함수
    public static LoginManager getInstance(){
        return instance;
    }


    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public void uploadImageToServer(){
        new ImageUploadTask(context,userData.getImage(),userData.getStNum()).execute();
    }

    public Context getContext(){
        return context;
    }
}
