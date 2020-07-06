package com.zabar.dartsv3;

import java.util.ArrayList;

public class Inbox {

    ArrayList<Message> messages;
    String myID;
    public Inbox(String myID){
        this.messages = new ArrayList<>();
        this.myID = myID;
    }

    public boolean addToInbox(Message msg){
        boolean shouldAdd = true;

        if(!msg.recvr.ID.equals(this.myID) && !msg.sndr.ID.equals(this.myID))
            return false;

        //getting the other person
        String otherGuy = msg.recvr.ID.equals(myID) ? msg.sndr.ID : msg.recvr.ID;

        for(int i = 0; i < this.messages.size(); i++){
            Message m = this.messages.get(i);
            String thisOtherGuy = m.recvr.ID.equals(myID) ? m.sndr.ID : m.recvr.ID;
            if(thisOtherGuy.equals(otherGuy)) {
                this.messages.remove(i);
                this.messages.add(0, msg);
                shouldAdd = false;
                break;
            }
        }
        if(shouldAdd)
            this.messages.add(msg);

        return shouldAdd;
    }
}
