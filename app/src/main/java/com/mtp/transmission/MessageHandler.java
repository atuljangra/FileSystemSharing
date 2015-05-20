package com.mtp.transmission;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mtp.connection.manager.Device;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.filesystemsharing.MainActivity;
import com.mtp.fsmanager.external.ExternalFSManager;

/**
 * Created by vivek on 3/5/15.
 */
public class MessageHandler {

    public MessageHandler(){

    }

   /* public void respond(String text){
        if(text == null || text.length() == 0){
            Log.d("msg handler","null of empty string");
            return;
        }
        Log.d("Message handler ", " responding to incoming message");
        FSMessage  msg = new FSMessage(text);
        respond(msg);
    }*/

    public void respond(FSMessage msg){
        if(msg.msgType == FSMessage.CHANGE){
            MainActivity.deviceManager.sendUpdates(msg);

        }else {
            Log.e("msg Handler","wrong msg");
        }
    }

    private class ExtFS implements Runnable{
        private ExternalFSManager extFS;
        public ExtFS(ExternalFSManager f){
            extFS = f;
        }
        public void run() {
            MainActivity.fileAdapter.addExternalFS(extFS);
        }
    }

    public  void respond(ClientListener client, FSMessage msg){

        switch(msg.msgType){
            case FSMessage.REQUESTFS:
                client.sendMsg(msg);
                break;
            case FSMessage.LOCALFS:
                Log.d("at client", "fs received");
                ExternalFSManager extFSMan = new ExternalFSManager(msg.msg);
                extFSMan.establishRelation(extFSMan.root,null);
                MainActivity.deviceManager.addExtFS(client.serverIP, extFSMan);
                new Handler(Looper.getMainLooper()).post(new ExtFS(extFSMan));

                break;
            case FSMessage.CHANGE:
                Log.d("Handler", "change received");
                Device dev = MainActivity.deviceManager.getDevice(client.serverIP);
                assert dev != null;
                dev.extFs.logChange(msg.msg);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.fileAdapter.refresh();
                    }
                });
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
