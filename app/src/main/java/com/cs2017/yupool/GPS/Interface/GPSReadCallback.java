package com.cs2017.yupool.GPS.Interface;

import android.location.Location;

/**
 * Created by cs2017 on 2017-12-02.
 */

public interface GPSReadCallback {
    public void onRead(double latitude,double longitude);
}
