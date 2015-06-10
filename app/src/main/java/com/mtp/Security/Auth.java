package com.mtp.Security;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Idea is to add a basic authentication system.
 * The Client will be authenticated by the server and then the file system
 * will be shared.
 * Authentication will be PIN based on both sides. This would be an initial auth step.
 * This step is needed to prevent the scenario when there is IP Spoofing in the initial step.
 * This is a small window but crucial window too. Because when this is established, then the connection
 * will be secure and there would be no way for a security breach.
 */
public class Auth {
    Popupwindow pp;
    Activity activity;

    public Auth(Activity ac) {
        this.activity = ac;
        pp = new Popupwindow(activity);

    }

    // Basic auth.
    // Auth on client side.
    public boolean authenticate_client(String ipAddress) {
        Log.d("Auth:", "Client auth for " + ipAddress);
        pp.init(0, ipAddress);
        pp.getPIN();
        return true;
    }

    // Auth on server side
    public boolean authenticate_server(final String ipAddress, final Object token) {
        synchronized (token) {
        Log.d("Auth:", "Server auth for " + ipAddress);

        pp.init(1, ipAddress);
        pp.getPIN();
        token.notify();
        return true;
        }
    }
}
