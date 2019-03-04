package com.cs2017.yupool.DriverRegister;

/**
 * Created by cs2017 on 2017-11-10.
 */

public class LicenseItem {
    private String city;
    private String name;
    private String cityCode;
    private String num1;
    private String num2;
    private String num3;
    private String jumin;
    private String secure;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getNum3() {
        return num3;
    }

    public void setNum3(String num3) {
        this.num3 = num3;
    }

    public String getJumin() {
        return jumin;
    }

    public void setJumin(String jumin) {
        this.jumin = jumin;
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getCityCode(){
        switch (city){
            case "경기":
                return "13";

            case "서울":
                return "11";

            case "경기북부":
                return "28";
            
            case "경기남부":
                return "13";
            
            case "강원":
                return "14";
            
            case "충북":
                return "15";
            
            case "전북":
                return "17";
            
            case "전남":
                return "18";
            
            case "경북":
                return "19";
            
            case "경남":
                return "20";
            
            case "제주":
                return "21";
            
            case "대구":
                return "22";
            
            case "인천":
                return "23";
            
            case "광주":
                return "24";
            
            case "대전":
                return "25";
            
            case "울산":
                return "26";
            
            case "부산":
                return "12";
            
        }
        return null;
    }
}
