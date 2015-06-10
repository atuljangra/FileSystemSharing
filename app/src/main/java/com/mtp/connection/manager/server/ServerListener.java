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
    private boolean authSuccess = false;
    int count = 0;  // count of the client threads;
    ServerSocket serverSocket;
    Activity activity;
    Object syncToken = new Object();
    public ServerListener(Activity activity, Auth auth){
        this.activity = activity;
        this.auth = auth;

    }

    @Override
    public void run() {
        while (true) try {
            serverSocket = new ServerSocket(SocketServerPORT);
            Log.d("Server listener", "I'm waiting here: " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();

            count++;
            String message = "#" + count + " from " + socket.getInetAddress()
                    + ":" + socket.getPort() + "\n";

            Log.d("server got message", message);

            // TODO:
            // Authentication should be done here. Proceed only if authenticated.
            /*
            synchronized (syncToken) {
                final String ipaddress = socket.getInetAddress().toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        authSuccess = auth.authenticate_server(ipaddress, syncToken);
                    }
                });

                try {
                    Log.d("Server", " waiting auth=" + authSuccess);
                    syncToken.wait();
                    Log.d("Server", "Woken up auth=" + authSuccess);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!authSuccess) {
                    Log.d("Server:", "Auth failed");
                    return;
                }
                authSuccess = false;

            }
            */
            // TODO Proceed if auth was successful;
            //TODO maintain this in some list
            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                    socket, count, activity);

            MainActivity.deviceManager.addConToServer(socket.getInetAddress().getHostAddress(),
                    socketServerReplyThread);

            socketServerReplyThread.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

