package com.mtp.fsmanager.internal;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.mtp.filesystemsharing.FileAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vivek on 3/18/15.
 * Manages all asspect of the local filesystem.
 */
public  class LocalFSManager {

    public MyFile root;

    private FSLogger logger;

    private int count;

    private boolean initialized = false;

    public LocalFSManager() {
        logger = new FSLogger();
    }

    public synchronized void initializeLocalFS() {
        if (isExternalStorageWritable())
            Log.d("Storage state", "External Storage writable");
        else
            Log.d("Storage state", "No external storage not writable");

        File file = Environment.getExternalStorageDirectory();
        Log.d("root", file.getName());
        count = 0;
        root = new MyFile();
        root.isDirectory = true;
        root.name = file.getName();
        root.path = file.getName();
        root.dirMonitor = new LocalFSMonitor(root, LocalFSMonitor.eventFlags, this);
        exploreStructure(root, file);
        String s = this.serialise();
        double t = s.length()/1024.0;
        Log.d("memory",Double.toString(t)+" kB");
        t = t/1024.0;
        Log.d("memory",Double.toString(t)+" mB");
        Log.d("count ", Integer.toString(count));
        initialized = true;


    }

    public synchronized Boolean isInitialized(){
        return initialized;
    }

    private void exploreStructure(MyFile root, File corrRoor) {
        File[] childList = corrRoor.listFiles();
        if (childList == null)
            return;

        for (File child : childList) {
            //Log.d("child" ,child.getName());
            MyFile newChild = new MyFile(root);
            newChild.name = child.getName();
            newChild.path = child.getPath();
            newChild.isDirectory = child.isDirectory();
            if(!MyFile.filter(newChild))
                continue;

            root.child.add(newChild);
            count++;
            if (newChild.isDirectory) {
                newChild.dirMonitor = new LocalFSMonitor(newChild, LocalFSMonitor.eventFlags, this);
                exploreStructure(newChild, child);
            }

        }

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void startWatching() {
        startWatching(this.root);
    }

    private void startWatching(MyFile root) {
        root.dirMonitor.startWatching();
        for (MyFile child : root.child) {
            if (child.isDirectory) {
                startWatching(child);
            }
        }
    }

    private void stopWatching(MyFile root) {
        root.dirMonitor.stopWatching();
        for (MyFile child : root.child) {
            if (child.isDirectory) {
                stopWatching(child);
            }
        }

    }

    public void stopWatching() {
        stopWatching(this.root);
    }

    public synchronized void create(MyFile parent, String file, boolean isdir) {
        ArrayList<MyFile> child = parent.child;
        MyFile f = new MyFile();
        f.name = file;
        f.path = parent.path + "/" + file;
        f.isDirectory = isdir;
        int event = Changes.CREATED;
        if (isdir) {
            f.dirMonitor = new LocalFSMonitor(f, LocalFSMonitor.eventFlags, this);
            f.dirMonitor.startWatching();
            event = event | Changes.ISDIR;
        }
        child.add(f);
        logger.addLog(f, event);
        Log.d("changes ", logger.serialize(-1));
    }

    public synchronized void delete(MyFile parent, String file) {
        ArrayList<MyFile> childList = parent.child;
        assert childList.size() > 0;

        for (MyFile child : childList) {
            if (child.name.equals(file)) {
                childList.remove(child);
                logger.addLog(child, Changes.DELETED);
                Log.d("changes ", logger.serialize(-1));
                return;
            }
        }

        throw new IllegalStateException("file deleted not found in structure");

    }

    public synchronized void addToAdaptor(FileAdapter adap, MyFile file){
        adap.addAll(file.child);
    }
    public String serialise() {
        Gson gson = new Gson();
        String result = gson.toJson(root);
        Log.d("json object ", Integer.toString(result.length()));
        return result;
    }

}
