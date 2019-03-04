package com.cs2017.yupool.ReqRecv.DO;

import android.os.Parcel;
import android.os.Parcelable;

import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.RoutePoint;

import java.io.Serializable;

/**
 * Created by cs2017 on 2017-12-01.
 */

public class WaitingItem implements Serializable, Parcelable {

    private int runFlag;
    private PoolItem poolItem;
    private String guestID;
    private RoutePoint routePoint;


    protected WaitingItem(Parcel in) {
        runFlag = in.readInt();
        guestID = in.readString();
    }

    public static final Creator<WaitingItem> CREATOR = new Creator<WaitingItem>() {
        @Override
        public WaitingItem createFromParcel(Parcel in) {
            return new WaitingItem(in);
        }

        @Override
        public WaitingItem[] newArray(int size) {
            return new WaitingItem[size];
        }
    };
    public WaitingItem(){};

    public RoutePoint getRoutePoint() {
        return routePoint;
    }

    public void setRoutePoint(RoutePoint routePoint) {
        this.routePoint = routePoint;
    }

    public String getGuestID() {
        return guestID;
    }

    public void setGuestID(String guestID) {
        this.guestID = guestID;
    }



    public int getRunFlag() {
        return runFlag;
    }

    public void setRunFlag(int runFlag) {
        this.runFlag = runFlag;
    }



    public PoolItem getPoolItem() {
        return poolItem;
    }

    public void setPoolItem(PoolItem poolItem) {
        this.poolItem = poolItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(runFlag);
        parcel.writeString(guestID);
    }

}

