package com.cs2017.yupool.PoolList.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cs2017.yupool.R;
import com.skp.Tmap.TMapPOIItem;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by cs2017 on 2017-11-08.
 */

public class SearchListAdapter extends BaseAdapter {
    private ArrayList<TMapPOIItem> list; // 풀 리스트 아이템 어레이리스트
    private Context context; // 어플리케이션 컨텍스트
    public SearchListAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
    }

    public void setArray(ArrayList<TMapPOIItem> list){
        this.list = list;
    }

    public void addItem(TMapPOIItem item){
        list.add(item);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TMapPOIItem getItem(int i) {
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
            view = inflater.inflate(R.layout.search_list_item,null);
        }
        TextView poiName = view.findViewById(R.id.poiname);
        TextView poiAddr = view.findViewById(R.id.poiaddress);

        TMapPOIItem item = list.get(i);
        poiName.setText(item.getPOIName());

        String addr = item.getPOIAddress().replaceAll("null","");
        poiAddr.setText(addr);
        return view;
    }
}
