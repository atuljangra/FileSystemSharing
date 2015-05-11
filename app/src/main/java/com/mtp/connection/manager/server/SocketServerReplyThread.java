package com.mtp.connection.manager.server;

import android.app.Activity;
import android.util.Log;

import com.mtp.filesystemsharing.UiUpdater;
import com.mtp.fsmanager.internal.LocalFSManager;
import com.mtp.transmission.Message;
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
    LocalFSManager fsManager;


    private boolean running = true;
    private String response = "";
    private MessageHandler msgHandler ;

    SocketServerReplyThread(Socket socket, int c , Activity activity, LocalFSManager fsManager) {
        hostThreadSocket = socket;
        cnt = c;
        this.activity = activity;
        this.fsManager = fsManager;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = "Hello from Android, you are #" + cnt;
        String message ="";

        //TODO modify this to output stream to seperate file
        try {
            outputStream = hostThreadSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            // send the initial filesystem

            Message msg = new Message(Message.lOCALFS,fsManager.serialise());
            msgReply = msg.serialize();
            printStream.print(msgReply);

            //printStream.close();
            message += "replayed: " + msgReply + "\n";
        } catch (IOException e) {

            e.printStackTrace();
            message += "Something wrong! " + e.toString() + "\n";
        }
        activity.runOnUiThread(new UiUpdater(activity, message));


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
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    int end = Message.searchEOM(response);
                    String txtMsg;
                    if(end != -1){
                        txtMsg = response.substring(0, end);
                        response = Message.getRemainingMsg(response, end);
                        msgHandler.respond(txtMsg);
                    }
                    //TODO need to identify end og message.
                    //TODO Need to add message handlers.
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
        }
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

    }
}
