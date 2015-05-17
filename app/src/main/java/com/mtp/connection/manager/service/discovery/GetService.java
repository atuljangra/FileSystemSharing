package com.mtp.connection.manager.service.discovery;

import android.app.Activity;
import android.util.Log;
import com.mtp.connection.manager.DeviceManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by vivek on 3/5/15.
 */
//TODO need to change service discovery
public class GetService extends Thread {
    Activity activity;
    ServiceSocket serviceSocket;
    DeviceManager dvManager;
    Boolean stop = false;

    public GetService(Activity actvity , ServiceSocket serviceSocket, DeviceManager dvManager){
        this.activity = actvity;
        this.serviceSocket = serviceSocket;
        this.dvManager = dvManager;
    }

    @Override
    public void run(){
        DatagramSocket socket = serviceSocket.serviceSocket;
        byte[] buff = new byte[256];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        while(!stop){
            try {
                socket.receive(packet);

                byte byteData[] = packet.getData();
                String receivedIp = new String(byteData, 0, packet.getLength());
                if(receivedIp.equals(RegisterService.getIpAddress().getHostAddress()))
                    continue;
                Log.d("ip received ", receivedIp);

                dvManager.addDevice(receivedIp, true);
                //TODO assume only ip is being sent

            } catch (SocketException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill(){
        stop = true;
    }
}

