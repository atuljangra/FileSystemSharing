package com.mtp.transmission;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.mtp.connection.manager.Device;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.filesystemsharing.MainActivity;
import com.mtp.fsmanager.external.ExternalFSManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private class OpenExtFile implements Runnable{
        File f;
        public OpenExtFile(File f){
            this.f = f;
        }
        public void run(){
            try {
                MainActivity.fileAdapter.openFile(f);
            }catch(IOException e){
                e.printStackTrace();
            }
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

            case FSMessage.REQUESTEDFILE:
                Gson g = new Gson();
                Log.d("file received",msg.msg);
                FileTransfer fT = g.fromJson(msg.msg, FileTransfer.class);
                FileOutputStream outputStream;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "temp/"+fT.name);

                    outputStream = new FileOutputStream(file);
                    outputStream.write(fT.data.getBytes());
                    outputStream.close();
                    new Handler(Looper.getMainLooper()).post(new OpenExtFile(file));
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

                break;
            default:
                Log.d("message handler:"+client.getName(),"unhandled message");
        }
    }
    public void respond(ClientListener client, String msg){
        respond(client,new FSMessage(msg));
    }

    private String sendFile(FSMessage msg){
        String path = msg.msg;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        byte [] bytearray  = new byte [(int)file.length()];
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream((int)file.length());
        String s = "";
        try{
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream bin = new BufferedInputStream(fin);
            bin.read(bytearray,0,bytearray.length);
            byteArrayOutputStream.write(bytearray, 0, bytearray.length);
            //s = byteArrayOutputStream.toString("UTF-8");
            s = new String(bytearray,"UTF-8");
        }catch(IOException e){
            e.printStackTrace();
        }

        FileTransfer fT = new FileTransfer();
        fT.name = file.getName();
        fT.data = s;
        return fT.serialize();


    }

    public void respond(SocketServerReplyThread server, FSMessage msg){
        switch(msg.msgType){
            case FSMessage.REQUESTFS:
                // send the initial filesystem
                FSMessage m = new FSMessage(FSMessage.LOCALFS, MainActivity.fsManager.serialise());
                server.sendMsg(m);
                break;
            case FSMessage.REQUESTFILE:
                FSMessage m1 = new FSMessage(FSMessage.REQUESTEDFILE,sendFile(msg));
                server.sendMsg(m1);
                break;

            default:
                Log.d("message handler:"+server.getName(),"unhandled message");
        }
    }
    public void respond(SocketServerReplyThread server, String msg){
        respond(server, new FSMessage(msg));
    }
}
