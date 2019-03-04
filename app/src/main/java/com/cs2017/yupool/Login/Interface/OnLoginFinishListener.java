package com.cs2017.yupool.Login.Interface;

import com.cs2017.yupool.Login.DO.UserData;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    로그인 결과를 받아오기 위한 콜벡 인터페이스
 */
public interface OnLoginFinishListener {
    public void onLoginFinish(boolean isSuccess, UserData userData,String error);
}
