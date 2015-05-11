package com.mtp.transmission;

import android.util.Log;

/**
 * Created by vivek on 3/5/15.
 */
public class MessageHandler {

    public MessageHandler(){

    }

    public void respond(String text){
        if(text == null || text.length() == 0){
            Log.d("msg handler","null of empty string");
            return;
        }
        Log.d("Message handler ", " responding to incoming message");
        Message  msg = new Message(text);
    }
}
