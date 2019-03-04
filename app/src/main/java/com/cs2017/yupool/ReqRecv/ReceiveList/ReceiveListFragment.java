package com.cs2017.yupool.ReqRecv.ReceiveList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.Database.Threads.GetWaitListRun;
import com.cs2017.yupool.UI.DataManager;
import com.cs2017.yupool.UI.DriverDetailActivity;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.R;
import com.cs2017.yupool.ReqRecv.Adapter.ReqListAdapter;
import com.cs2017.yupool.ReqRecv.DO.WaitingItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    사용자 여정 내역을 보여주는 프래그먼트
 */
public class ReceiveListFragment extends Fragment {

    private View rootView;

    private ListView listView;
    private ReqListAdapter reqListAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_request_list,container,false); // 부모에 붙임

        listView = rootView.findViewById(R.id.listView);
        reqListAdapter = new ReqListAdapter(getContext(),true);
        listView.setAdapter(reqListAdapter);

        ArrayList<WaitingItem> list = DatabaseManager.getInstance().getWaitList(LoginManager.getInstance().getUserData().getStNum(), GetWaitListRun.DRIVER);
        if(list == null){
            Toast.makeText(getContext(),"서버 통신 에러",Toast.LENGTH_SHORT).show();
        }else{
            reqListAdapter.setList(list);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    WaitingItem item = reqListAdapter.getItem(i);
                    if(item!=null){
                        Intent intent = new Intent(getContext(), DriverDetailActivity.class);
                        DataManager.getInstance().setItem(item);
                        intent.putExtra("item", (Serializable) item);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(),"에러!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Log.d("ReceiveListFragment","initView");
        return rootView;
    }
}
