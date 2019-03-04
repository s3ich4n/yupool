package com.cs2017.yupool.Login;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by User on 2016-11-14.
 */

// 이미지를 업로드 하기위한 객체
public class ImageCUploadTask extends AsyncTask<Object,Boolean,Boolean>{

    private static final String TAG = "ImageCUploadTask";

    private Context context;

    // 업로드 될 서버의 php 경로
    private static final String upLoadServerUri = "http://alsdn.iptime.org:9986/yupool/upload.php";
    private static final String lineEnd = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****";
    private static final int maxBufferSize = 1 * 1024 * 1024;
    private int serverResponseCode = 0;
    private String id;
    private HttpURLConnection conn;
    private DataOutputStream dos;
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private ByteArrayInputStream fileInputStream;

    public ImageCUploadTask(Context context, Bitmap bitmap, String id ){
        // 생성자
        conn = null;
        dos = null;
        this.id = id;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,10,bos);
        byte[] bitmapdate =bos.toByteArray();
        this.fileInputStream = new ByteArrayInputStream(bitmapdate);
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Object[] params) {
        boolean success = false;
        try {
            // URL 을 초기화
            URL url = new URL(upLoadServerUri);

            // URL 에 대해 연결을 시도한다.
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // 인풋을 허용함
            conn.setDoOutput(true); // 아웃풋을 허용함
            conn.setUseCaches(true); // 캐쉬된것을 쓰지 않음
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", id);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ id + "_car.jpg\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i(TAG,"HTTP Response is " + serverResponseMessage + " :  " + serverResponseCode);

            if(serverResponseCode == 200) success = true;

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException e) {
            Log.e(TAG,"URL 주소 오류");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG,"IO 익셉션");
            e.printStackTrace();
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}