package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.PoolRegister.PoolItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

import static com.cs2017.yupool.StaticVal.TOMCAT_URL;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class GetPhoneRun extends DBRun {
    private static final String TAG = "GetPhoneRun";

    private String id;
    private String phone;

    public GetPhoneRun(String id){
        this.id = id;
        phone = null;
    }


    @Override
    public void run() {
        try {
            Connection.Response res = null;
            res = Jsoup.connect(TOMCAT_URL+"phone.jsp")
                    .method(Connection.Method.GET)
                    .data("id",id)
                    .cookies(DatabaseManager.getInstance().getCookie())
                    .execute();
            Log.d(TAG,"서버 응답["+res.statusCode()+"]");
            checkSuccess(res);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(res.body());
            for(int i = 0 ; i < jsonArray.size() ; i ++){
                JSONObject object = new JSONObject();
                object = (JSONObject) jsonArray.get(i);
                phone = ((String) object.get("phone"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String getPhone() {
        return phone;
    }
}
