package com.cs2017.yupool.UI;

import com.cs2017.yupool.ReqRecv.DO.WaitingItem;

/**
 * Created by cs2017 on 2017-12-02.
 */

public class DataManager {

    private static DataManager instance = new DataManager();
    private WaitingItem item;
    private DataManager(){};

    public static DataManager getInstance(){
        return instance;
    }

    public WaitingItem getItem() {
        return item;
    }

    public void setItem(WaitingItem item) {
        this.item = item;
    }

}
