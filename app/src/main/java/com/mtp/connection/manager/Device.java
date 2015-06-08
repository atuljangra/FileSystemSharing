package com.mtp.connection.manager;

import com.mtp.connection.manager.client.ClientConnectionManager;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.fsmanager.external.ExternalFSManager;

import java.net.Socket;

/**
 * Created by vivek on 3/5/15.
 * Maintians the details of the device in the network
 * and service it offers
 * If I am a client of the device
 * If the device is my client
 */
public class Device {
    public String ip;

    public boolean isSharingFS = false;
    public ClientConnectionManager conToClient =  null;
    public SocketServerReplyThread conToServer = null;
    public ExternalFSManager extFs = null;

    public  Device(String ip, Boolean isSharingFS){
        this.ip = ip;
        this.isSharingFS = isSharingFS;
    }

    @Override
    public String toString(){
        return ip;
    }
}
