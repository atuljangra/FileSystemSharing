package com.mtp.connection.manager.server;

import android.app.Activity;
import com.mtp.filesystemsharing.UiUpdater;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by vivek on 3/5/15.
 * Waits for incoming data from the client
 */
public class SocketServerReplyThread extends Thread {
    private Socket hostThreadSocket;
    int cnt;
    Activity activity;

    SocketServerReplyThread(Socket socket, int c , Activity activity) {
        hostThreadSocket = socket;
        cnt = c;
        this.activity = activity;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = "Hello from Android, you are #" + cnt;
        String message ="";

        //TODO modify it to listen for incoming packets

        try {
            outputStream = hostThreadSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(msgReply);
            printStream.close();
            message += "replayed: " + msgReply + "\n";
        } catch (IOException e) {

            e.printStackTrace();
            message += "Something wrong! " + e.toString() + "\n";
        }
        activity.runOnUiThread(new UiUpdater(activity, message));
    }
}
