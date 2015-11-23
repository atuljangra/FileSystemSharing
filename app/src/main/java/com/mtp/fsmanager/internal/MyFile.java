package com.mtp.fsmanager.internal;

import android.os.Environment;

import com.mtp.filesystemsharing.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 17/3/15.
 */
public class MyFile implements Serializable {

    public String name;
    public transient String path;
    public Boolean isDirectory;
    public ArrayList<MyFile> child;
    transient LocalFSMonitor dirMonitor;
    public transient MyFile parent;
    public transient int depth;

    public MyFile(){
        child = new ArrayList<MyFile>();
        parent = null;
        depth = 0;
    }

    public MyFile(MyFile parent){
        child = new ArrayList<MyFile>();
        this.parent = parent;
        depth = parent.depth + 1;
    }

    public static boolean filter(MyFile file){
        if(file.depth > 1)
            return true;
        if(file.depth == 1){
            if(file.name.equals(Environment.DIRECTORY_DCIM) || file.name.equals(Environment.DIRECTORY_DOCUMENTS)
                    || file.name.equals(Environment.DIRECTORY_DOWNLOADS) || file.name.equals(Environment.DIRECTORY_MOVIES)
                    || file.name.equals(Environment.DIRECTORY_MUSIC) || file.name.equals(Environment.DIRECTORY_PICTURES)
                    || file.name.equals(Environment.DIRECTORY_RINGTONES) || file.name.equals(MainActivity.publicName))
                return true;
        }
        return false;

    }
}
