package com.zabar.dartsv3;

public class Server {
    public static final String PROTOCOL = "https://";
    public static final String IP = "darts-web-server.herokuapp.com";
    public static final int PORT = 3000;
//    public static String getUrl(){
//        return PROTOCOL + IP + ":" + PORT;
//    }

    public static final String TPLMAPS_ROUTE_URL = "http://api.tplmaps.com:8888/route?" +
            "reroute=false&pointsencoded=false" +
            "&apikey=$2a$10$EAVaIVvXe3gESbS8skWZZe4vpCIazE7q5sDUpL3P1HgyTS5wD8Y8q";


    public static String getUrl(){
        return PROTOCOL + IP ;
    }
}

