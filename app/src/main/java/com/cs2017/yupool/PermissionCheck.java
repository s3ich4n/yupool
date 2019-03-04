package com.cs2017.yupool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs2017 on 2017-10-02.
 */

public class PermissionCheck {
    private static final String TAG = "PermissionCheck";
    private static final int REQUEST_CODE = 100;

    private final static PermissionCheck instance = new PermissionCheck();
    private PermissionCheck(){};
    public static PermissionCheck getInstance(){return instance;}

    private Activity activity;
    private String failMessage;

    public void checkPermission(Activity activity,String[] permissions){
        List<String> requestList = new ArrayList<String>();
        this.activity = activity;
        for(int i = 0 ; i < permissions.length ; i++ ) {
            if(ContextCompat.checkSelfPermission(activity,permissions[i]) != PackageManager.PERMISSION_GRANTED){
                // 권한이 없는 경우에만 requestList 에 넣은 후 해당 권한들을 요청
                requestList.add(permissions[i]);
            }
        }
        if(requestList.size()!=0)
            ActivityCompat.requestPermissions(activity,requestList.toArray(new String[requestList.size()]),REQUEST_CODE);
    }

    public void resultOfRequest(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // 권한 요청 결과를 받아 거부된 권한은 다시 요청
        if(requestCode == REQUEST_CODE){
            List<String> deniedPermissions = new ArrayList<String>();
            for(int i = 0 ; i < permissions.length ; i++){
                if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                    // Never ask 안된 것만 추가
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissions[i])){
                        deniedPermissions.add(permissions[i]);
                    }else{
                        Log.e(TAG,"Permanantly Disabled : " + permissions[i]);
                    }
                }
            }
            if(deniedPermissions.size()!=0)
                reCheckPermission(deniedPermissions.toArray(new String[deniedPermissions.size()]));
        }
    }

    private void reCheckPermission(String[] permissions){
        Log.d(TAG,"ReCheckPermission");
        if(failMessage!=null)
            Toast.makeText(activity,failMessage,Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(activity,permissions,REQUEST_CODE);
    }

    public void setFailMessage(String failMessage){
        this.failMessage = failMessage;
    }
}
