package com.mtp.connection.manager.service.discovery;

import android.app.Activity;
import android.util.Log;

import com.mtp.connection.manager.DeviceManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


/**
 * Created by vivek on 4/5/15.
 */
public class RegisterService extends Thread {
    Activity activity;
    ServiceSocket serviceSocket;
    public static int HEARTBLEED_TIME = 1000; // 1 seconds
    Boolean stop = false;

    public RegisterService(Activity actvity , ServiceSocket serviceSocket){
        this.activity = actvity;
        this.serviceSocket = serviceSocket;

    }

    @Override
    public void run(){
        DatagramSocket socket = serviceSocket.serviceSocket;
        String msgToSend = getIpAddress();
        while(!stop){
            try {
                DatagramPacket packet = new DatagramPacket(msgToSend.getBytes(),
                        msgToSend.length(), InetAddress.getByName("192.168.49.255"),
                                                                        ServiceSocket.servicePort);
                socket.send(packet);
                Thread.sleep(HEARTBLEED_TIME);

            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    public void kill(){
        stop = true;
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

}
