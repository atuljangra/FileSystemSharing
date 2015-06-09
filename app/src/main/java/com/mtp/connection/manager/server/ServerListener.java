package com.mtp.connection.manager.server;

import android.app.Activity;
import android.util.Log;


import com.mtp.Security.Auth;
import com.mtp.filesystemsharing.MainActivity;

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
    private Auth auth;
    int count = 0;  // count of the client threads;
    ServerSocket serverSocket;
    Activity activity;

    public ServerListener(Activity activity, Auth auth){
        this.activity = activity;
        this.auth = auth;

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

                Log.d("server got message", message);

                // TODO:
                // Authentication should be done here. Proceed only if authenticated.
                // auth.authenticate_server(socket.getInetAddress().toString());

                //TODO maintain this in some list
                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count,activity);

                MainActivity.deviceManager.addConToServer(socket.getInetAddress().getHostAddress(),
                                                            socketServerReplyThread);

                socketServerReplyThread.run();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

