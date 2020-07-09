package com.zabar.dartsv3;

import com.google.firebase.database.DataSnapshot;

import java.sql.Date;
import java.sql.Timestamp;

class Message {
    String ID,  message;
    QRUnit sndr, recvr;
    Timestamp time;

    public Message(String ID, QRUnit recvr, QRUnit sndr, String message, Timestamp time) {
        this.ID = ID;
        this.recvr = recvr;
        this.sndr = sndr;
        this.message = message;
        this.time = time;
    }
}
