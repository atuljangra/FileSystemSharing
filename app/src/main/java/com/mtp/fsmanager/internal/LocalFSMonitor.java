package com.mtp.fsmanager.internal;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by vivek on 28/4/15.
 */
public class LocalFSMonitor extends FileObserver {

    public static int eventFlags = ( FileObserver.CREATE | FileObserver.DELETE ) ;
    private LocalFSManager fsManager;



    public MyFile monitoredDir;

    public LocalFSMonitor(MyFile file, LocalFSManager fsManager){
        super(file.path);
        monitoredDir = file;
        this.fsManager = fsManager;
    }

    public LocalFSMonitor(MyFile file,int mask, LocalFSManager fsManager){
        super(file.path,mask);
        monitoredDir = file;
        this.fsManager = fsManager;
    }

    public void onEvent(int event,String path){
        Log.d("event ",Integer.toHexString(event));
        boolean isDir = (event & (~FileObserver.ALL_EVENTS)) > 0 ? true:false;
        event = event & FileObserver.ALL_EVENTS;


        switch(event){

            case FileObserver.DELETE:
                Log.d("FS Monitor " ,"delete");
                fsManager.delete(monitoredDir,path);
                break;

            case FileObserver.CREATE:
                Log.d("FS Monitor " ,"create event");
                fsManager.create(monitoredDir, path, isDir);
                break;

            default:
                Log.d("FS Monitor " ,"other event");
                break;

        }

        if(path==null)
            return;

        Log.d("fs changed ", monitoredDir.path+"/"+path);
    }

    @Override
    protected void finalize(){
        super.finalize();
        Log.d("listener deleted ","GC");
    }

}
