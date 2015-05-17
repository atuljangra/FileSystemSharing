package com.mtp.connection.manager.server;

import android.app.Activity;
import android.util.Log;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by vivek on 3/5/15. This is the main server thread to listen for incomming connections
 * and accept them. It launches new threads to handle the new connection
 */
//TODO create message sender
public class ServerListener extends Thread{
    public String MyIp;
    public  static final int SocketServerPORT = 8080;

    int count = 0;  // count of the client threads;
    ServerSocket serverSocket;
    Activity activity;

    public ServerListener(Activity activity){
        this.activity = activity;

    }

    @Override
    public void run() {
        while (true) {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                Log.d("Server listener", "I'm waiting here: " + serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();

                count++;
                String message = "#" + count + " from " + socket.getInetAddress()
                        + ":" + socket.getPort() + "\n";

                Log.d("server message", message);

                //TODO maintain this in some list
                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count,activity);
                socketServerReplyThread.run();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

