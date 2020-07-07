package com.zabar.dartsv3;

public class Server {
    public static final String PROTOCOL = "http://";
    public static final String IP = "192.168.0.100";
    public static final int PORT = 3000;
    public static String getUrl(){
        return PROTOCOL + IP + ":" + PORT;
    }
}
