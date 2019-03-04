package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.R;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

/**
 * Created by cs2017 on 2017-11-09.
 */

public class DBRun implements Runnable {
    private static final String TAG = "DBRun";
    protected boolean success = false;

    @Override
    public void run() {

    }

    public boolean isSuccess(){
        return success;
    }

    protected boolean checkSuccess(Connection.Response res){
        Log.d(TAG,"쿠키 테스트 : "+ DatabaseManager.getInstance().getCookie().toString());


        if(res.statusCode() ==200) {
            success = true;
            return true;
        }
        else{
            success = false;
            Log.e(TAG,"서버 에러 ["+res.statusCode()+"]" +":" +res.statusMessage());
            return false;
        }

    }
}
