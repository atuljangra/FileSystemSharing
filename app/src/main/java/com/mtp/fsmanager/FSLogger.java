package com.mtp.fsmanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 30/4/15.
 */
public class FSLogger {
    private class Snapshot implements Serializable{
        public int snapshot_id;
        public ArrayList<Changes> change = new ArrayList<Changes>();

    }

    private class Changes implements Serializable {
        public transient int CREATED = 0x01;
        public transient int DELETED = 0x02;
        public transient int ISDIR = 0x04;
        public int event;
        public String path;
    }

    ArrayList<Snapshot> fsSnapshots = new ArrayList<Snapshot>();


    String serialize(int prev_snapID){
        String result = null;
        return result;
    }

    

}
