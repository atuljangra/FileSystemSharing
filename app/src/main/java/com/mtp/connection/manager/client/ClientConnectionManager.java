package com.mtp.connection.manager.client;

import com.mtp.connection.manager.server.ServerListener;
import com.mtp.transmission.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by vivek on 9/5/15.
 */
public class ClientConnectionManager {
    private String serverIP;
    private Socket socket = null;
    ClientListener receiverThread;

    public ClientConnectionManager(String ip){
        this.serverIP = ip;
    }

    public void startListening(){
        assert socket == null;
        /*try{
            socket = new Socket(serverIP, ServerListener.SocketServerPORT);
        }catch(IOException e){
            e.printStackTrace();
        }*/

        receiverThread = new ClientListener(serverIP);
        receiverThread.start();

    }

    public void sendMessage(Message msg){
        //TODO might need to convert to asynch task
    }
}
