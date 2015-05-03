package com.mtp.connection.manager.client;

import com.mtp.connection.manager.server.ServerListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by vivek on 3/5/15. Establishes connection to the server.
 * Listens to the incoming messages.
 */
//TODO create message sender
public class ClientListener extends Thread{
    private String serverIP;
    private Socket socket = null;
    private boolean running = true;
    private String response = "";

    public ClientListener(String serverIP){
        this.serverIP = serverIP;
    }

    @Override
    public void run(){
        while(running) {
            try {
                //TODO Need to save the Listener with the server
                socket = new Socket(serverIP, ServerListener.SocketServerPORT);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

				/*
				 * notice:
				 * inputStream.read() will block if no data return
				 */
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
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

    }
}
