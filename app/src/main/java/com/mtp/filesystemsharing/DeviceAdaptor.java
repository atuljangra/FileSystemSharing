package com.mtp.filesystemsharing;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.vivek.filesystemsharing.R;
import com.mtp.connection.manager.Device;

import java.util.ArrayList;

/**
 * Created by vivek on 5/5/15.
 */
public class DeviceAdaptor extends ArrayAdapter<Device> {

    private Context context;
    private ArrayList<Device> devices;
    public DeviceAdaptor(Context context, int res, ArrayList<Device> devices ){
        super(context, R.layout.devicelist, devices);
        this.context = context;
        this.devices = devices;
    }

}
