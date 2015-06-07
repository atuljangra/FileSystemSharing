package com.mtp.filesystemsharing;

import android.app.Activity;
import android.widget.TextView;

import com.example.vivek.filesystemsharing.R;

/**
 * Created by vivek on 3/5/15.
 */
public class UiUpdater implements Runnable{
    String msg;
    Activity activity;
    public UiUpdater(Activity activity, String msg) {
        this.msg = msg;
        this.activity = activity;
        // TODO Auto-generated constructor stub
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        TextView text =  (TextView) activity.findViewById(R.id.textview);

        text.setText("Internet provider" + msg);
    }

}