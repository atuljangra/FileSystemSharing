package com.mtp.connection.manager.service.discovery;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by vivek on 4/5/15.
 * This is the socket initialization for sending and receiving service broadcast
 */
public class ServiceSocket {
    public DatagramSocket serviceSocket;
    public static final int servicePort = 8081;
    public ServiceSocket(){
        try {
            serviceSocket = new DatagramSocket(servicePort);
            serviceSocket.setBroadcast(true);
        }catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * there is a race between service listener kill and this
     */
    public void kill(){
        serviceSocket.close();
    }
}
