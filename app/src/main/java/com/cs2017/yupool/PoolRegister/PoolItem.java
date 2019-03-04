package com.cs2017.yupool.PoolRegister;

import java.io.Serializable;

/**
 * Created by cs2017 on 2017-11-26.
 */

public class PoolItem implements Serializable{

    private String idx;
    private String id;
    private String date;
    private String comment;
    private String startDate;
    private String fromName;
    private String fromLati;
    private String fromLongi;
    private String fromAddr;
    private String toName;
    private String toLati;
    private String toLongi;
    private String toAddr;
    private boolean isUsing;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromLati() {
        return fromLati;
    }

    public void setFromLati(String fromLati) {
        this.fromLati = fromLati;
    }

    public String getFromLongi() {
        return fromLongi;
    }

    public void setFromLongi(String fromLongi) {
        this.fromLongi = fromLongi;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToLati() {
        return toLati;
    }

    public void setToLati(String toLati) {
        this.toLati = toLati;
    }

    public String getToLongi() {
        return toLongi;
    }

    public void setToLongi(String toLongi) {
        this.toLongi = toLongi;
    }


    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }
    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

}
