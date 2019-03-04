package com.cs2017.yupool.PoolList;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by cs2017 on 2017-11-27.
 */

public class PoolDetailDialog {
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private View rootView;

    private ImageView profileIV;
    private TextView fromTV;
    private TextView toTV;
    private TextView timeTV;
    private TextView commentTV;
    private Button applypoolBT;

    public PoolDetailDialog(final Context context, PoolItem item){
        builder = new AlertDialog.Builder(context);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_pool_detail,null);
        builder.setView(rootView);

        profileIV = rootView.findViewById(R.id.profileIV);
        fromTV = rootView.findViewById(R.id.startplaceTV);
        toTV = rootView.findViewById(R.id.endplaceTV);
        timeTV = rootView.findViewById(R.id.starttimeTV);
        commentTV = rootView.findViewById(R.id.commentTV);
        applypoolBT = rootView.findViewById(R.id.applypoolBT);

        Glide.with(context).load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getId()+".jpg")
                .bitmapTransform(new CropCircleTransformation(context))
                .into(profileIV);
        fromTV.setText(item.getFromName());
        toTV.setText(item.getToName());
        commentTV.setText(item.getComment());
        timeTV.setText(item.getStartDate());


        applypoolBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"신청되었습니다",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        dialog = builder.create();
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
