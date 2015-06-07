package com.mtp.connection.manager.Security;

import android.app.Activity;

/**
 * Created by atul on 6/7/15.
 */
public class Auth {
    Popupwindow pp;
    Activity activity;

    public Auth(Activity ac) {
        this.activity = ac;
        pp = new Popupwindow(activity);

    }
    // Basic auth.
    public boolean authenticate() {
        pp.getPIN();
        return false;

    }


}
