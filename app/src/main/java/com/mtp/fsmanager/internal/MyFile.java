package com.mtp.fsmanager.internal;

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

    public MyFile(){
        child = new ArrayList<MyFile>();
        parent = null;
    }

    public MyFile(MyFile parent){
        child = new ArrayList<MyFile>();
        this.parent = parent;
    }
}
