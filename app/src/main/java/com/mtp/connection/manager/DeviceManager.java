package com.mtp.connection.manager;

import android.app.Activity;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by vivek on 3/5/15.
 * Maintains the list of devices and the service they offer
 * Might separate clients and the servers
 */
public class DeviceManager {
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
    public ArrayList<Device> getList(){
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
                deviceAdaptor.notifyDataSetChanged();
            }
        });
    }
}


