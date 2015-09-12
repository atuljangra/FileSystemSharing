package com.mtp.transmission;

import com.google.gson.Gson;

import java.io.Serializable;

public class FSMessage implements Serializable {
    public int msgType;
    public String msg;


    /* To be sent by Client 1-20*/
    final public static transient int REQUESTFS = 1;
    final public static transient int REQUESTFILE = 2;
    final public static transient int BEINACTIVE = 3;
    final public static transient int BEACTIVE = 4;

    /* To be sent by Server 21+ */
    final public static transient int LOCALFS = 21;
    //final public static transient int CHANGE = 22;
    final public static transient int CHANGES = 23;


    //TODO need to ensure that this pattern is not present in the data being sent. need to find other initialization
    /* this is to be sent with every message to mark its end */
    public static String MSGENDIDENTYFIER = "44394318";
    private static Gson gson;

    public FSMessage(String msg){
        gson = new Gson();
        FSMessage m = gson.fromJson(msg,FSMessage.class);
        this.msgType = m.msgType;
        this.msg = m.msg;
    }

    public FSMessage(int msgType, String msg){
        this.msgType = msgType;
        this.msg = msg;
        gson = new Gson();
    }

    public String serialize(){
        return gson.toJson(this)+MSGENDIDENTYFIER;
    }

    /*Searches for end of message . if not found then returns -1*/
    public static int searchEOM(String text){
        //TODO might need to improve this
        int i = text.indexOf(MSGENDIDENTYFIER);
        return i;
    }

    public static String getRemainingMsg(String text,int indexEOM){
        String remMsg = text.substring(indexEOM + MSGENDIDENTYFIER.length());
        return  remMsg;
    }




}