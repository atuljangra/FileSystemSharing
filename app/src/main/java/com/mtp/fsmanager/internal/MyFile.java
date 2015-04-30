package com.mtp.fsmanager.internal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 17/3/15.
 */
public class MyFile implements Serializable{

    public String name;
    public transient String path;
    public Boolean isDirectory;
    transient LocalFSMonitor  dirMonitor;
    public ArrayList<MyFile> child;
    public MyFile(){
        child = new ArrayList<MyFile>();
    }

}
