package com.cs2017.yupool.ReqRecv.Adapter;

import android.os.AsyncTask;
import android.widget.TextView;

import com.skp.Tmap.TMapData;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cs2017 on 2017-12-01.
 */

public class GetAddressTask extends AsyncTask {
    private TextView textView;
    private String address;
    private double latitude;
    private double longitude;
    public GetAddressTask(TextView textView,double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.textView = textView;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        TMapData tMapData = new TMapData();
        try {
            address = tMapData.convertGpsToAddress(latitude,longitude);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        textView.setText(address);
        super.onPostExecute(o);
    }
}
