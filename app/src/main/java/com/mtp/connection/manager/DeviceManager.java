package com.mtp.connection.manager;

import java.util.ArrayList;

/**
 * Created by vivek on 3/5/15.
 * Maintains the list of devices and the service they offer
 * Might separate clients and the servers
 */
public class DeviceManager {
    private ArrayList<Device> deviceList;

    public synchronized void  addDevice(String ip, Boolean isSharingFS){
        for(Device dev:deviceList){
            if(dev.ip.equals(ip)){
                dev.isSharingFS = isSharingFS;
                return;
            }
        }

        Device dev = new Device(ip,isSharingFS);
        deviceList.add(dev);
    }
}


