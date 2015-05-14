package com.mtp.transmission;

import android.util.Log;

import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.filesystemsharing.MainActivity;

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
        FSMessage  msg = new FSMessage(text);
        respond(msg);
    }

    public void respond(FSMessage msg){

    }

    public void respond(ClientListener client, FSMessage msg){

        switch(msg.msgType){
            case FSMessage.REQUESTFS:
                client.sendMsg(msg);
                break;
            case FSMessage.LOCALFS:
                Log.d("at client","fs received");
                break;
            default:
                Log.d("message handler:"+client.getName(),"unhandled message");
        }
    }
    public void respond(ClientListener client, String msg){
        respond(client,new FSMessage(msg));
    }
    public void respond(SocketServerReplyThread server, FSMessage msg){
        switch(msg.msgType){
            case FSMessage.REQUESTFS:
                // send the initial filesystem
                FSMessage m = new FSMessage(FSMessage.LOCALFS, MainActivity.fsManager.serialise());
                server.sendMsg(m);
                break;
            /*case FSMessage.LOCALFS:
                Log.d("at client","fs received");
                break;*/
            default:
                Log.d("message handler:"+server.getName(),"unhandled message");
        }
    }
    public void respond(SocketServerReplyThread server, String msg){
        respond(server, new FSMessage(msg));
    }
}
