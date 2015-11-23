package com.mtp.transmission;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mtp.connection.manager.Device;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.filesystemsharing.MainActivity;
import com.mtp.fsmanager.external.ExternalFSManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

   /* public void respond(FSMessage msg){
        if(msg.msgType == FSMessage.CHANGES){
            MainActivity.deviceManager.sendUpdates(msg);

        }else {
            Log.e("msg Handler","wrong msg");
        }
    }*/

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
            /*case FSMessage.CHANGE:
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
                break;*/
            case FSMessage.CHANGES:
                //TODO handle multiple changes
                Log.d("Handler", "change received");
                Device dev1 = MainActivity.deviceManager.getDevice(client.serverIP);
                assert dev1 != null;
                if(msg.msg == null)
                    break;
                dev1.extFs.logChanges(msg.msg);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.fileAdapter.refresh();
                    }
                });

                break;
            /*case FSMessage.REQUESTEDFILE:
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

                break;*/
            default:
                Log.d("message handler:"+client.getName(),"unhandled message");
        }
    }
    public void respond(ClientListener client, String msg){
        respond(client,new FSMessage(msg));
    }

    private void sendFile(FSMessage msg,String ip){
        Socket socket = null;
        boolean connected = false;

        while(!connected) {
            try {
                socket = new Socket(ip, 8082);
                connected = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(5000);
                }catch (InterruptedException e1){
                    e1.printStackTrace();
                }
            }
        }

        try {

            OutputStream outputStream = socket.getOutputStream();

            FileInputStream inputStream = null;
            File f = new File(Environment.getExternalStorageDirectory(),msg.msg);
            inputStream = new FileInputStream(f);
            int len;
            byte buf[]  = new byte[1024*1024];
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            //f = new File(Environment.getExternalStorageDirectory(),"/DCIM/t.jpg");

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public void respond(SocketServerReplyThread server, FSMessage msg){

        switch(msg.msgType){
            case FSMessage.REQUESTFS:
                // send the initial filesystem
                FSMessage m = new FSMessage(FSMessage.LOCALFS, MainActivity.fsManager.serialise());
                server.sendMsg(m);
                break;
            case FSMessage.REQUESTFILE:

                sendFile(msg,server.getIP());
                break;
            case FSMessage.BEINACTIVE:
                Device dev = MainActivity.deviceManager.getDevice(server.getIP());
                dev.isActive = false;
                //TODO change state to inactive
                break;
            case FSMessage.BEACTIVE:
                //message will contain the previous id
                Device dev2 = MainActivity.deviceManager.getDevice(server.getIP());
                dev2.isActive = true;
                FSMessage m1 = new FSMessage(FSMessage.CHANGES, MainActivity.fsManager.logger.serialize(Integer.valueOf(msg.msg)));
                server.sendMsg(m1);
                //TODO change state to active
                break;
            default:
                Log.d("message handler:"+server.getName(),"unhandled message");
        }
    }
    public void respond(SocketServerReplyThread server, String msg){
        respond(server, new FSMessage(msg));
    }
}
