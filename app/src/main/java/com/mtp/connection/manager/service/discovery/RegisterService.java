package com.mtp.connection.manager.service.discovery;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mtp.connection.manager.DeviceManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by vivek on 4/5/15.
 */
public class RegisterService extends Thread {
    Activity activity;
    ServiceSocket serviceSocket;
    public static int HEARTBLEED_TIME = 10000; // 10 seconds
    Boolean stop = false;

    public RegisterService(Activity actvity , ServiceSocket serviceSocket){
        this.activity = actvity;
        this.serviceSocket = serviceSocket;

    }

    @Override
    public void run(){
        DatagramSocket socket = serviceSocket.serviceSocket;
        InetAddress ip = getIpAddress();
        String broadAddr = getBroadcast(ip);
        String msgToSend = ip.getHostAddress();

        while(!stop){
            try {
                DatagramPacket packet = new DatagramPacket(msgToSend.getBytes(),
                        msgToSend.length(), InetAddress.getByName(broadAddr),
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

    public static InetAddress getIpAddress() {
        String ip = "";
       // InetAddress t;
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
                        ip += inetAddress.getHostAddress() ;
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return null;
    }
    public String getBroadcast(InetAddress inetAddr) {

        NetworkInterface temp;
        InetAddress iAddr = null;
        try {
            temp = NetworkInterface.getByInetAddress(inetAddr);
            List<InterfaceAddress> addresses = temp.getInterfaceAddresses();

            for (InterfaceAddress inetAddress: addresses)

                iAddr = inetAddress.getBroadcast();
            Log.d("broadcast ", "iAddr=" + iAddr.getHostAddress());
            return iAddr.getHostAddress();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return "";
    }
   /* private InetAddress getBroadcastAddress() throws IOException {
        WifiManager myWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();

        if (myDhcpInfo == null) {
            System.out.println("Could not get broadcast address");
            return null;
        }
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }*/
}
