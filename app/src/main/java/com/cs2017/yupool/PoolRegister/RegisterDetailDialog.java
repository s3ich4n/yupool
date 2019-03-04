package com.cs2017.yupool.PoolRegister;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.FragmentUtil.FragmentManager;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cs2017 on 2017-11-27.
 */

public class RegisterDetailDialog {
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private View rootView;

    private TextView fromTV;
    private TextView toTV;
    private EditText timeET;
    private EditText commentET;
    private Button commitBT;

    public RegisterDetailDialog(final Context context){
        builder = new AlertDialog.Builder(context);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_register_detail,null);
        builder.setView(rootView);

        fromTV = rootView.findViewById(R.id.startplaceTV);
        toTV = rootView.findViewById(R.id.endplaceTV);
        timeET = rootView.findViewById(R.id.starttimeET);
        commentET = rootView.findViewById(R.id.commentET);
        commitBT = rootView.findViewById(R.id.registerpoolBT);

        fromTV.setText(MapViewController.getInstance().getFromItem().getPOIName());
        toTV.setText(MapViewController.getInstance().getToItem().getPOIName());

        commitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timeET.getText().length() < 10 && commentET.getText().length() <100){
                    Toast.makeText(context,"출발시간과 커멘터를 10자이상 입력해 주세요",Toast.LENGTH_SHORT).show();
                }else{
                    PoolItem registerItem= new PoolItem();
                    registerItem.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                    registerItem.setId(LoginManager.getInstance().getUserData().getStNum());
                    registerItem.setComment(commentET.getText().toString());
                    registerItem.setStartDate(timeET.getText().toString());
                    registerItem.setFromName(MapViewController.getInstance().getFromItem().getPOIName());
                    registerItem.setFromLati(String.valueOf(MapViewController.getInstance().getFromItem().getPOIPoint().getLatitude()));
                    registerItem.setFromLongi(String.valueOf(MapViewController.getInstance().getFromItem().getPOIPoint().getLongitude()));
                    registerItem.setFromAddr(MapViewController.getInstance().getFromItem().getPOIAddress().replaceAll("null",""));
                    registerItem.setToName(MapViewController.getInstance().getToItem().getPOIName());
                    registerItem.setToLati(String.valueOf(MapViewController.getInstance().getToItem().getPOIPoint().getLatitude()));
                    registerItem.setToLongi(String.valueOf(MapViewController.getInstance().getToItem().getPOIPoint().getLongitude()));
                    registerItem.setToAddr(MapViewController.getInstance().getToItem().getPOIAddress().replaceAll("null",""));
                    registerItem.setUsing(false);
                    if(DatabaseManager.getInstance().registerPool(registerItem)){
                        Toast.makeText(context,"등록이 완료되었습니다",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"서버 통신 불가",Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
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
