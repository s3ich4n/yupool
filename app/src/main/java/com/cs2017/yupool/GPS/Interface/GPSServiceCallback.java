package com.cs2017.yupool.GPS.Interface;

import android.location.Location;

/**
 * Created by cs2017 on 2017-11-07.
 */

// 액티비티에서 GPS 서비스 간 콜백 인터페이스
public interface GPSServiceCallback {
    // 현재 위치 변경시 콜백
    public void onChangeCurrentLocation(Location location);
}
