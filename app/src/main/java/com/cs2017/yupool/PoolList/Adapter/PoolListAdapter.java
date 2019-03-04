package com.cs2017.yupool.PoolList.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs2017.yupool.R;
import com.cs2017.yupool.PoolRegister.PoolItem;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    카풀 리스트의 아답터
 */
public class PoolListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<PoolItem> list = new ArrayList<>();; // 풀 리스트 아이템 어레이리스트

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<PoolItem> filteredItemList = list;

    Filter searchFilter;


    private Context context; // 어플리케이션 컨텍스트
    public PoolListAdapter(Context context){
        this.context = context;
    }

    public void setList(ArrayList<PoolItem> list){
        this.list = list;
        this.filteredItemList = list;
    }

    public void addItem(PoolItem item){
        list.add(item);
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public PoolItem getItem(int i) {

        //return list.get(i);
        return filteredItemList.get(i);
    }

    public PoolItem getItemFromIDX(String IDX){
        for(PoolItem item : list){
            if(item.getIdx().equals(IDX))
                return item;
        }
        return null;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
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
            view = inflater.inflate(R.layout.pool_list_item,null);
        }
        // find view by id 를 한번 씩 하기 위해 Wrapper class 사용
        TextView from = view.findViewById(R.id.from);
        TextView to = view.findViewById(R.id.to);
        TextView time =view.findViewById(R.id.time);
        ImageView image = view.findViewById(R.id.image);


        //PoolItem item = list.get(i);
        PoolItem item = filteredItemList.get(i);
        from.setText(item.getFromName());
        to.setText(item.getToName());
        time.setText(item.getStartDate());
        Glide.with(context).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getId()+".jpg")
                .bitmapTransform(new CropCircleTransformation(context))
                .into(image);
        /*Glide.with(context).load(R.drawable.test)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(image);*/
        return view;
    }

    @Override
    public Filter getFilter() {
        System.err.println("getFilter");
        if( searchFilter ==null){
            searchFilter = new SearchFilter();
        }
        return searchFilter;
    }


    /* 검색 기능을 위한 필터 */
    private class SearchFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;
            if (constraint == null || constraint.length() == 0) {
                results.values = list ;
                results.count = list.size() ;
                System.err.println("필터 Null");
            }else{
                String s = constraint.toString();
                double lat1 = Double.valueOf(s.substring(0, s.indexOf(':')));
                double lon1 = Double.valueOf(s.substring(s.indexOf(':')+1, s.length()));
                ArrayList<PoolItem> searchList= new ArrayList<>();

                System.err.println("필터 시작 "+ s);
                System.err.println("lat::" + lat1);
                System.err.println("lon::" + lon1);

                for(PoolItem item : list){
                    double lat2 = Double.valueOf(item.getFromLati());
                    double lon2 = Double.valueOf(item.getFromLongi());
                    double theta, dist;

                    theta = lon1 - lon2;
                    dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = dist * 60 * 1.1515;
                    dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
                    dist = dist * 1000.0;      // 단위  km 에서 m 로 변환
                    if( dist <= 1000.0) {
                        searchList.add(item);
                        System.err.println("매치 : " + item.getFromAddr());
                    }
                }
                results.values = searchList;
                results.count = searchList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            filteredItemList = (ArrayList<PoolItem>)results.values;
            //notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }






}








