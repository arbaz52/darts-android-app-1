package com.zabar.dartsv3;

public class Server {
    public static final String PROTOCOL = "https://";
    public static final String IP = "darts-web-server.herokuapp.com";
    public static final int PORT = 3000;
//    public static String getUrl(){
//        return PROTOCOL + IP + ":" + PORT;
//    }

    public static String getUrl(){
        return PROTOCOL + IP ;
    }
}

