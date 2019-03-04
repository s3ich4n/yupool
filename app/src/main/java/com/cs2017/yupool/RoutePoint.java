package com.cs2017.yupool;

/**
 * Created by cs2017 on 2017-12-01.
 */

public class RoutePoint  {
    private double startLati;
    private double startLongi;
    private double money;

    public double getStartLati() {
        return startLati;
    }

    public void setStartLati(double startLati) {
        this.startLati = startLati;
    }

    public double getStartLongi() {
        return startLongi;
    }

    public void setStartLongi(double startLongi) {
        this.startLongi = startLongi;
    }

    public double getEndLati() {
        return endLati;
    }

    public void setEndLati(double endLati) {
        this.endLati = endLati;
    }

    public double getEndLongi() {
        return endLongi;
    }

    public void setEndLongi(double endLongi) {
        this.endLongi = endLongi;
    }

    private double endLati;
    private double endLongi;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
