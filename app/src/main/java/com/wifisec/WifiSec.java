package com.wifisec;

public class WifiSec {
    public String wifi_title;
    public String wifi_bssid;
    public int wifi_security;
    public String wifi_password;
    public double wifi_coordinates_x;
    public double wifi_coordinates_y;

    public WifiSec(String wifi_title, String wifi_bssid, int wifi_security, String wifi_password, double wifi_coordinates_x, double wifi_coordinates_y){
        this.wifi_title = wifi_title;
        this.wifi_security = wifi_security;
        this.wifi_bssid = wifi_bssid;
        this.wifi_password = wifi_password;
        this.wifi_coordinates_x = wifi_coordinates_x;
        this.wifi_coordinates_y = wifi_coordinates_y;
    }

    public String getssid(){
        return this.wifi_title;
    }

    public String getbssid(){
        return this.wifi_bssid;
    }

    public String getpassword(){
        return this.wifi_password;
    }

    public double getcoordinatesx(){
        return this.wifi_coordinates_x;
    }

    public double getcoordinatesy(){
        return this.wifi_coordinates_y;
    }
}