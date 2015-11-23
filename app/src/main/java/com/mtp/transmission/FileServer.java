package com.mtp.transmission;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.mtp.filesystemsharing.FileAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by vivek on 21/5/15.
 */
public class FileServer extends Thread {

    String dstAddress;
    int dstPort;
    String response = "";
    File f;
    String name;
    public static int SERVERPORT = 8082;
    FileAdapter adap;

    public FileServer(String name, FileAdapter adap) {
        this.name = name;
        this.adap = adap;
        this.setPriority(Thread.MAX_PRIORITY);
    }


    public void run() {

        Socket socket = null;

        try {
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);

            Socket client = serverSocket.accept();

            f = new File(Environment.getExternalStorageDirectory() , "/temp/"+name);

					/*File dirs = new File(f.getParent());
					if (!dirs.exists())
						dirs.mkdirs();
					f.createNewFile();*/
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();


            //}
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    adap.openFile(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void copyFile(InputStream in, FileOutputStream out){
        int len;
        byte buf[]  = new byte[1024*1024];
        try{
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }



}