package com.cs2017.yupool.AlarmDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by User on 2017-03-26.
 */

public class AlarmDialog {
    private static AlertDialog dialog;
    private static final AlarmDialog instance = new AlarmDialog();
    private AlarmDialog(){};

    public static AlarmDialog getInstance(){
        return instance;
    }
    public void show(Context context, String title, String message){
        dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
