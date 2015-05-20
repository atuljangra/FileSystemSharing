package com.mtp.connection.manager;

import android.app.Activity;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.SocketServerReplyThread;
import com.mtp.filesystemsharing.FileAdapter;
import com.mtp.filesystemsharing.MainActivity;
import com.mtp.fsmanager.external.ExternalFSManager;
import com.mtp.transmission.FSMessage;

import java.util.ArrayList;

/**
 * Created by vivek on 3/5/15.
 * Maintains the list of devices and the service they offer
 * Might separate clients and the servers
 */
public class DeviceManager  {
    private ArrayList<Device> deviceList;
    private ArrayAdapter<Device> deviceAdaptor;
    private Activity activity;

    public DeviceManager(Activity activity){
        this.activity = activity;
        deviceList = new ArrayList<Device>();
    }

    public void setAdap(ArrayAdapter<Device> adap){
        deviceAdaptor = adap;
    }
    public synchronized ArrayList<Device> getList(){
        return deviceList;
    }

    public synchronized void  addDevice(String ip, Boolean isSharingFS){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                dev.isSharingFS = isSharingFS;
                return;
            }
        }

        Device dev = new Device(ip,isSharingFS);
        deviceList.add(dev);
        // Notify the main thread.
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //In case get service is not called
                if(deviceAdaptor == null)
                    return;
                deviceAdaptor.notifyDataSetChanged();
            }
        });
    }

    public synchronized void remove(Device d){
        //TODO to be removed by service discovery if timestamp expires.
        // TODO Need to remove extFS from file adaptor too
    }

    public synchronized void addConToServer(String ip, SocketServerReplyThread server){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                dev.conToServer = server;
                return;
            }
        }

        Device dev = new Device(ip,false);
        deviceList.add(dev);
        dev.conToServer = server;

    }

    public synchronized void removeConToServer(String ip){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                dev.conToServer = null;
                return;
            }
        }
        Log.e("Remove ServConn","device not found");

    }

    public synchronized void addExtFS(String ip, ExternalFSManager extFs){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                dev.extFs = extFs;
                return;
            }
        }
        Log.e("Remove ServConn","device not found");
    }

    public synchronized void addToAdaptor(FileAdapter adap){
        for(Device dev: deviceList){
            if(dev.extFs!= null)
                adap.add(dev.extFs.root);
        }
    }

    public synchronized void sendUpdates(FSMessage msg){

        for(Device dev: deviceList){
            dev.conToServer.sendMsg(msg);
        }
    }

    public Device getDevice(String ip){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                return dev;
            }
        }
        Log.e("Remove ServConn","device not found");
        return null;
    }

    public Device getDevice(ExternalFSManager ext){
        for(Device dev:deviceList){
            if(dev.extFs == ext){
                return dev;
            }
        }
        Log.e("Remove ServConn","device not found");
        return null;
    }
}


