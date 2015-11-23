package com.mtp.Security;

/**
 * Created by atul on 6/7/15.
 */

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.vivek.filesystemsharing.R;

public class Popupwindow {
    PopupWindow popUp;
    Activity activity;
    EditText pass;
    Button okButton;
    String password;
    TextView label;
    public Popupwindow (Activity activity) {
        this.activity = activity;
    }

    public void init(int server, String ipAddress) {
        if(server == 1)
            init_server(ipAddress);
        else init_client(ipAddress);

    }
    public void init_client(String ipAddress) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup, null, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        popUp = new PopupWindow(popUpView, width, 3 *height/4, true);
        popUp.setContentView(popUpView);
        label = (TextView)popUpView.findViewById(R.id.label);
        label.setText("Enter the PIN for " + ipAddress);
        pass = (EditText) popUpView.findViewById(R.id.passEntry);
        okButton = (Button) popUpView.findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read the password.
                password = pass.getText().toString();
                Log.d("Popup", "PIN is " + password);
                pass.setText("");
                popUp.dismiss();
            }
        });
    }

    // This is called from serverThread and is executed on main UI thread. So we need a way to tranfer the data.
    public void init_server(String ipAddress) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup, null, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        popUp = new PopupWindow(popUpView, width, 3 *height/4, true);
        popUp.setContentView(popUpView);

        label = (TextView)popUpView.findViewById(R.id.label);
        label.setText("Enter the same pin as " + ipAddress);
        pass = (EditText) popUpView.findViewById(R.id.passEntry);
        okButton = (Button) popUpView.findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read the password.
                password = pass.getText().toString();
                Log.d("Popup", "PIN is " + password);
                pass.setText("");
                popUp.dismiss();
            }
        });
    }

    public String getPIN() {
        popUp.showAtLocation(activity.getCurrentFocus(), Gravity.BOTTOM, 0, 0);
        return password;
    }
}

