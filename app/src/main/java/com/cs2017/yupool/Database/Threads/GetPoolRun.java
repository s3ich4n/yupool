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

public class GetPoolRun extends DBRun {
    private static final String TAG = "GetPoolRun";

    private String id;
    private ArrayList<PoolItem> list;

    public GetPoolRun(String id){
        this.id = id;
        list = new ArrayList<>();
    }


    @Override
    public void run() {
        try {
            Connection.Response res = null;
            if(id == ""){
                res = Jsoup.connect(TOMCAT_URL+"poollist.jsp")
                        .method(Connection.Method.GET)
                        .cookies(DatabaseManager.getInstance().getCookie())
                        .execute();
            }else{
                res = Jsoup.connect(TOMCAT_URL+"poollist.jsp")
                        .method(Connection.Method.GET)
                        .cookies(DatabaseManager.getInstance().getCookie())
                        .data("id",id)
                        .execute();
            }
            Log.d(TAG,"서버 응답["+res.statusCode()+"]");
            checkSuccess(res);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(res.body());
            for(int i = 0 ; i < jsonArray.size() ; i ++){
                JSONObject object = new JSONObject();
                object = (JSONObject) jsonArray.get(i);
                PoolItem poolItem = new PoolItem();
                poolItem.setIdx((String) object.get("idx"));
                poolItem.setId((String) object.get("id"));
                poolItem.setDate((String) object.get("regdate"));
                poolItem.setComment((String) object.get("comment"));
                poolItem.setFromName((String) object.get("fromname"));
                poolItem.setFromLati((String) object.get("fromlati"));
                poolItem.setFromLongi((String) object.get("fromlongi"));
                poolItem.setFromAddr((String) object.get("fromaddr"));
                poolItem.setToAddr((String) object.get("toaddr"));
                poolItem.setToLati((String) object.get("tolati"));
                poolItem.setToLongi((String) object.get("tolongi"));
                poolItem.setToName((String) object.get("toname"));
                poolItem.setToAddr((String) object.get("toaddr"));
                poolItem.setStartDate((String) object.get("startdate"));
                if(object.get("isusing").equals("0"))
                    poolItem.setUsing(false);
                else
                    poolItem.setUsing(true);
                list.add(poolItem);
            }

        } catch (IOException e) {
            e.printStackTrace();
            list = null;
        } catch (ParseException e) {
            e.printStackTrace();
            list = null;
        }
    }
    public ArrayList<PoolItem> getList(){
        return list;
    }
}
