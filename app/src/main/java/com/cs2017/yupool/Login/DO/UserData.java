package com.cs2017.yupool.Login.DO;

import android.graphics.Bitmap;

/**
 * Created by cs2017 on 2017-11-05.
 */

public class UserData {
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStNum() {
        return stNum;
    }

    public void setStNum(String stNum) {
        this.stNum = stNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private Bitmap image;
    private String name;
    private String stNum;
    private String phone;
    private boolean isDriver;

    public boolean isDriver() {
        return isDriver;
    }

    public void setIsDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }
}
