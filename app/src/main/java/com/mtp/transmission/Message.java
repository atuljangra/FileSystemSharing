package com.mtp.transmission;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by vivek on 3/5/15.
 */
public class Message implements Serializable {
    int msgType;
    String msg;


    /* To be sent by Client 1-20*/
    public static transient int REQUESTFS = 1;

    /* To be sent by Server 21+ */
    public static transient int lOCALFS = 21;
    public static transient int CHANGE = 22;

    //TODO need to ensure that this pattern is not present in the data being sent. need to find other initialization
    /* this is to be sent with every message to mark its end */
    public static String MSGENDIDENTYFIER = "44394318";
    private static Gson gson;

    public Message(String msg){
        gson = new Gson();
        Message m = gson.fromJson(msg,Message.class);
        this.msgType = m.msgType;
        this.msg = m.msg;
    }

    public Message(int msgType, String msg){
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
