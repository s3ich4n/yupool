package com.cs2017.yupool.UseList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs2017.yupool.R;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    사용자 여정 내역을 보여주는 프래그먼트
 */
public class emptyfragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_use_list,container,false); // 부모에 붙임
        return view;
    }
}
