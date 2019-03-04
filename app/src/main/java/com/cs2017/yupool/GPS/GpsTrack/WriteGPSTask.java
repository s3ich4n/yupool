package com.cs2017.yupool.GPS.GpsTrack;

import android.os.AsyncTask;
import android.util.Log;

import com.cs2017.yupool.StaticVal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-10-02.
 */

public class WriteGPSTask extends AsyncTask {

    private static final String TAG = "WriteGPSTask";
    private String lati;
    private String longi;
    private String idx;
    private Map<String,String> cookie;

    public WriteGPSTask(String idx, double longi, double lati, Map<String,String> cookie){
        this.idx = idx;
        this.longi = String.valueOf(longi);
        this.lati = String.valueOf(lati);
        this.cookie = cookie;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Connection.Response res = Jsoup.connect(TOMCAT_URL+"gpsw.jsp")
                    .data("idx",idx)
                    .data("longi",longi)
                    .data("lati",lati)
                    .cookies(cookie)
                    .method(Connection.Method.POST)
                    .execute();
            if(res.statusCode() == 200){
                Log.d(TAG,"[서버 송신 완료] :" + res.statusCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
