package com.cs2017.yupool.FCM;

/**
 * Created by cs2017 on 2017-11-27.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.cs2017.yupool.UI.DialogActivity;
import com.cs2017.yupool.R;
import com.cs2017.yupool.UI.MainActivity;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FBMsgService";


    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //추가한것
        String message = remoteMessage.getData().get("message").toString();
        String title = remoteMessage.getData().get("title").toString();
        String type = remoteMessage.getData().get("type").toString();
        sendNotification(message,title);
        System.out.println("타입 : " + type);
        alertDialog(message,title,type);
    }

    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void alertDialog(String message, String title,String type){
        Intent intent = new Intent(this, DialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title",title);
        intent.putExtra("msg",message);
        intent.putExtra("type",type);
        startActivity(intent);
    }

}

