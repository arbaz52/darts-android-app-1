package com.zabar.dartsv3;

class Message {
    String ID,  message;
    User sndr, recvr;

    public Message(String ID, User recvr, User sndr, String message) {
        this.ID = ID;
        this.recvr = recvr;
        this.sndr = sndr;
        this.message = message;
    }
}
