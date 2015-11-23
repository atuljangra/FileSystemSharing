package com.mtp.connection.manager.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.mtp.connection.manager.server.ServerListener;
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
 * Created by vivek on 3/5/15. Establishes connection to the server.
 * Listens to the incoming messages.
 */
//TODO create message sender
public class ClientListener extends Thread {

    private boolean running = true;
    private String response = "";
    Socket socket;
    private  MessageHandler msgHandler ;
   /* public ClientListener(Socket socket){
        this.socket = socket;
        msgHandler = new MessageHandler();
    }
*/
    public String serverIP;
    private SendingHandler sendHandler;
    private Sender senderThread;
    OutputStream outputStream;

   public ClientListener(String ip){
       super(ip);
       setPriority(Process.THREAD_PRIORITY_BACKGROUND);
       this.serverIP = ip;
   }
    @Override
    public void run(){
        msgHandler = new MessageHandler();
        senderThread = new Sender();
        senderThread.start();

        //This is to ensure that looper has been prepared before handling messages;
        while(senderThread.getLooper() == null){};

        while(running) {
            try {
                //TODO Need to save the Listener with the serve
                socket = new Socket(serverIP, ServerListener.SocketServerPORT);

                /* Based on initial conf send pull request */
                FSMessage m = new FSMessage(FSMessage.REQUESTFS, socket.getLocalAddress().getHostAddress());
                msgHandler.respond(this, m);


                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024*10);
                byte[] buffer = new byte[1024*10];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

				/*
				 * notice:
				 * inputStream.read() will block if no data return
				 */

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    String packet = byteArrayOutputStream.toString("UTF-8");
                    int end = FSMessage.searchEOM(packet);
                    response += packet;
                    String txtMsg;
                    if (end != -1) {
                        end = response.length() - packet.length() + end;
                        txtMsg = response.substring(0, end);
                        response = FSMessage.getRemainingMsg(response, end);
                        msgHandler.respond(this, txtMsg);
                    }
                    //TODO need to identify end og message.
                    //TODO Need to add message handlers.
                }

            } catch (UnknownHostException e) {
                //Connection is disconnected for some reason. try to restore it
                try {
                    Thread.sleep(5000);
                }catch(InterruptedException e1){
                    e1.printStackTrace();
                }
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }



        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* to clear the resources */
    public void kill(){
        running = false;
        senderThread.quit();

    }

    public void sendMsg(FSMessage msg){
// TODO
        /*
        '   java.lang.NullPointerException: Attempt to invoke virtual method 'android.os.Message com.mtp.connection.manager.client.ClientListener$SendingHandler.obtainMessage(int, java.lang.Object)' on a null object reference
            at com.mtp.connection.manager.client.ClientListener.sendMsg(ClientListener.java:127)
            at com.mtp.transmission.MessageHandler.respond(MessageHandler.java:84)
            at com.mtp.connection.manager.client.ClientListener.run(ClientListener.java:62)

         */
        Message m = sendHandler.obtainMessage(1,msg);
        m.sendToTarget();
    }

    private class Sender extends HandlerThread{
        public Sender(){
            super("sender "+serverIP, Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        protected void onLooperPrepared(){
            super.onLooperPrepared();
            sendHandler = new SendingHandler(getLooper());
        }

    }

    private class SendingHandler extends Handler {
        public SendingHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message m){
            FSMessage msg = (FSMessage) m.obj;
            String message = "";

            try {
                outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                // send the initial filesystem


                String msgReply = msg.serialize();
                printStream.print(msgReply);

                //printStream.close();
                message += "replayed: " + msgReply + "\n";
            } catch (IOException e) {

                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
           Log.d("sent by client ", message);

        }
    }


}
