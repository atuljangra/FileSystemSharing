package com.mtp.connection.manager.server;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.mtp.filesystemsharing.MainActivity;
import com.mtp.transmission.FSMessage;
import com.mtp.transmission.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by vivek on 3/5/15.
 * Waits for incoming data from the client
 */
public class SocketServerReplyThread extends Thread {
    private Socket hostThreadSocket;
    int cnt;
    Activity activity;



    private boolean running = true;
    private String response = "";
    private MessageHandler msgHandler;
    private SendingHandler sendHandler;
    private Sender senderThread;
    OutputStream outputStream;
    String ip;

    SocketServerReplyThread(Socket socket, int c , Activity activity) {
        super(socket.getInetAddress().getHostName());
        hostThreadSocket = socket;
        cnt = c;
        this.activity = activity;
        ip = socket.getInetAddress().getHostAddress();

    }

    public String getIP(){
        return hostThreadSocket.getInetAddress().getHostAddress();
    }
    @Override
    public void run() {


        msgHandler = new MessageHandler();
        senderThread = new Sender();
        senderThread.start();

        //This is to ensure that looper has been prepared before handling messages;
        while(senderThread.getLooper() == null){};

        /*Listening thread*/
        while(running) {
            try {
                //TODO do something if socket gets closed
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = hostThreadSocket.getInputStream();

            /*
             * notice:
             * inputStream.read() will block if no data return
             */
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    int end = FSMessage.searchEOM(response);
                    String txtMsg;
                    if(end != -1){
                        txtMsg = response.substring(0, end);
                        response = FSMessage.getRemainingMsg(response, end);
                        int prevPrio = this.getPriority();
                        this.setPriority(Thread.MAX_PRIORITY);
                        msgHandler.respond(this, txtMsg);
                        this.setPriority(prevPrio);
                    }
                    //TODO need to identify end og message.
                    //TODO Need to add message handlers.
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
                //Connection terminated;
                break;
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
        }

        kill();
        if(hostThreadSocket != null){
            Log.d("Server reply thread ", "closing socket");
            try {
                hostThreadSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* to clear the resources */
    public void kill(){

        running = false;
        Looper.myLooper().quit();
        MainActivity.deviceManager.removeConToServer(ip);

    }

    public void sendMsg(FSMessage msg){
        Message m = sendHandler.obtainMessage(1,msg);
        m.sendToTarget();
    }



    private class Sender extends HandlerThread{
        public Sender(){
            super("sender "+hostThreadSocket.getInetAddress().getHostName(),
                    Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        protected void onLooperPrepared(){
            super.onLooperPrepared();
            sendHandler = new SendingHandler(getLooper());
        }

    }

    private class SendingHandler extends Handler{
        public SendingHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message m){
            FSMessage msg = (FSMessage) m.obj;
            String message = "";
            //TODO modify this to output stream to seperate file
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);


                String msgReply = msg.serialize();
                printStream.print(msgReply);

                //printStream.close();
                message += "replayed: " + msgReply + "\n";
            } catch (IOException e) {

                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
            Log.d("server reply", message);

        }
    }


}
