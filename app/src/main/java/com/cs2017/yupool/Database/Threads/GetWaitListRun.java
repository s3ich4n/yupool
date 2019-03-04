package com.cs2017.yupool.Database.Threads;

import android.util.Log;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;
import com.cs2017.yupool.RoutePoint;
import com.cs2017.yupool.StaticVal;

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

public class GetWaitListRun extends DBRun {
    private static final String TAG = "GetWaitListRun";
    public static final int GUEST = 0;
    public static final int DRIVER = 1;
    private String id;
    private int type;
    private ArrayList<WaitingItem> list;

    public GetWaitListRun(String id,int type){
        this.id = id;
        this.type =type;
        list = new ArrayList<>();
    }


    @Override
    public void run() {
        try {
            Connection.Response res = null;
            if(type == GUEST){
                res = Jsoup.connect(TOMCAT_URL+"waitinglist.jsp")
                        .method(Connection.Method.GET)
                        .cookies(DatabaseManager.getInstance().getCookie())
                        .data("id",id)
                        .data("type","g")
                        .execute();
            }else if(type == DRIVER){
                res = Jsoup.connect(TOMCAT_URL+"waitinglist.jsp")
                        .method(Connection.Method.GET)
                        .cookies(DatabaseManager.getInstance().getCookie())
                        .data("id",id)
                        .data("type","d")
                        .execute();
            }
            Log.d(TAG,"서버 응답["+res.statusCode()+"]");
            checkSuccess(res);
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(res.body());
            for(int i = 0 ; i < jsonArray.size() ; i ++){
                JSONObject object = new JSONObject();
                object = (JSONObject) jsonArray.get(i);
                WaitingItem waitingItem = new WaitingItem();
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


                waitingItem.setPoolItem(poolItem);

                RoutePoint routePoint = new RoutePoint();
                routePoint.setMoney(Double.parseDouble((String) object.get("price")));
                routePoint.setStartLati(Double.parseDouble((String) object.get("slati")));
                routePoint.setStartLongi(Double.parseDouble((String) object.get("slongi")));
                routePoint.setEndLati(Double.parseDouble((String) object.get("elati")));
                routePoint.setEndLongi(Double.parseDouble((String) object.get("elongi")));
                waitingItem.setRoutePoint(routePoint);

                if(object.get("runflag").equals("0"))
                    waitingItem.setRunFlag(StaticVal.WAITING_ACCEPT);
                else if(object.get("runflag").equals("1"))
                    waitingItem.setRunFlag(StaticVal.WAITING_DRIVE);
                else if(object.get("runflag").equals("2"))
                    waitingItem.setRunFlag(StaticVal.DRIVING);
                waitingItem.setGuestID((String) object.get("guestid"));
                list.add(waitingItem);
            }

        } catch (IOException e) {
            e.printStackTrace();
            list = null;
        } catch (ParseException e) {
            e.printStackTrace();
            list = null;
        }
    }
    public ArrayList<WaitingItem> getList(){
        return list;
    }
}
