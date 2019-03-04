package com.cs2017.yupool.GPS.GpsTrack;

import android.util.Log;

import com.cs2017.yupool.GPS.GPSService;
import com.cs2017.yupool.GPS.Interface.GPSReadCallback;
import com.cs2017.yupool.StaticVal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimerTask;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-10-02.
 */

public class ReadGPSTask extends TimerTask {
    private static final String TAG = "ReadGPSTask";
    private String idx;
    private Map<String,String> cookie;

    //레퍼런스 -> GPS Service 를 통해 MainActivity callback 을 호출하기 위함
    private GPSReadCallback gpsReadCallback;

    public ReadGPSTask(String idx, Map<String,String> cookie, GPSReadCallback gpsReadCallback){
        this.idx = idx;
        this.cookie = cookie;
        this.gpsReadCallback = gpsReadCallback;
    }

    @Override
    public void run() {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"gpsr.jsp")
                    .data("idx",idx)
                    .cookies(cookie)
                    .method(Connection.Method.GET)
                    .execute();
            if(res.body().toString().matches("FAIL")){
                Log.e(TAG,"[서버 수신 실패]");
            }else{
                Log.d(TAG,"[서버 수신 완료]" + res.body().toString());
                StringTokenizer st = new StringTokenizer( res.body().toString(),"/");
                try{
                    gpsReadCallback.onRead(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
