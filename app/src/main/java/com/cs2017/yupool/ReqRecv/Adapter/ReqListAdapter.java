package com.cs2017.yupool.ReqRecv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs2017.yupool.R;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;
import com.cs2017.yupool.StaticVal;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapData;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    카풀 리스트의 아답터
 */
public class ReqListAdapter extends BaseAdapter {

    private ArrayList<WaitingItem> list = new ArrayList<>();; // 풀 리스트 아이템 어레이리스트
    private Context context; // 어플리케이션 컨텍스트
    private boolean isDriver = false;
    private TMapData tMapData = new TMapData();



    public ReqListAdapter(Context context, boolean isDriver){
        this.context = context;
        this.isDriver = isDriver;
    }

    public void setList(ArrayList<WaitingItem> list){
        this.list = list;
    }

    public void addItem(WaitingItem item){
        list.add(item);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WaitingItem getItem(int i) {
        return list.get(i);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            // 최초 view 가 없을때 inflate 하고, 이후에는 이미 있던 view 들을 재활용
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.req_list_item,null);
        }

        // find view by id 를 한번 씩 하기 위해 Wrapper class 사용
        final TextView from = view.findViewById(R.id.fromTV);
        final TextView to = view.findViewById(R.id.toTV);
        TextView price = view.findViewById(R.id.priceTV);
        TextView status = view.findViewById(R.id.statusTV);
        ImageView image = view.findViewById(R.id.image);
        //TextView time =view.findViewById(R.id.time);


        final WaitingItem item = list.get(i);
        //time.setText(item.getStartDate());
        if(isDriver){
            // 드라이버의 경우 게스트의 출발. 목적 보여주야함
            GetAddressTask getAddressTask1 = new GetAddressTask(from,item.getRoutePoint().getStartLati(), item.getRoutePoint().getStartLongi());
            GetAddressTask getAddressTask2 = new GetAddressTask(to,item.getRoutePoint().getEndLati(), item.getRoutePoint().getEndLongi());

            getAddressTask1.execute();
            getAddressTask2.execute();
            price.setText(Util.formatMoney(item.getRoutePoint().getMoney())+"원");
            if(item.getRunFlag() == StaticVal.WAITING_ACCEPT){
                status.setText("수락대기");
            }else if(item.getRunFlag() == StaticVal.WAITING_DRIVE){
                status.setText("운행대기");
            }else if(item.getRunFlag() == StaticVal.DRIVING){
                status.setText("운행중");
            }
            Glide.with(context).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getGuestID()+".jpg")
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(image);
        }else{
            // 게스트일 경우, 드라이버의 운행정보 보여줘야함
            from.setText(item.getPoolItem().getFromName());
            to.setText(item.getPoolItem().getToName());
            price.setText(Util.formatMoney(item.getRoutePoint().getMoney())+"원");
            if(item.getRunFlag() == StaticVal.WAITING_ACCEPT){
                status.setText("수락대기");
            }else if(item.getRunFlag() == StaticVal.WAITING_DRIVE){
                status.setText("운행대기");
            }else if (item.getRunFlag() == StaticVal.DRIVING){
                status.setText("운행중");
            }
            Glide.with(context).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getPoolItem().getId()+".jpg")
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(image);
        }
        return view;
    }

}








