package com.mtp.Security;

/**
 * Created by atul on 6/7/15.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.filesystemsharing.R;

public class Popupwindow {
    PopupWindow popUp;
    Activity activity;
    EditText pass;
    Button okButton;
    String password;
    public Popupwindow (Activity activity) {
        this.activity = activity;
        init();
    }

    public void init() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup, null, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        popUp = new PopupWindow(popUpView, width, 3 *height/4, true);
        popUp.setContentView(popUpView);
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

    public int getPIN() {
        popUp.showAtLocation(activity.getCurrentFocus(), Gravity.BOTTOM, 0, 0);
        return 0;
    }
}

