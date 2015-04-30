package com.mtp.fsmanager;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 30/4/15.
 */

//TODO  The size of snapshots can grow large. Need to flush or persist.
public class FSLogger {

    public static int CREATED = 0x01;
    public static int DELETED = 0x02;
    public static int ISDIR = 0x04;

    private FSIdAllocator idAllocator;

    private Gson gson ;

    private ArrayList<Snapshot> fsSnapshots = new ArrayList<Snapshot>();

    public FSLogger(){
        idAllocator = new FSIdAllocator();
        gson = new Gson();
        Snapshot snap = new Snapshot();
        snap.snapshot_id = 0;
        fsSnapshots.add(snap);

    }

    private class Snapshot implements Serializable{
        public int snapshot_id;
        public ArrayList<Changes> change = new ArrayList<Changes>();

    }

    private class Changes implements Serializable {

        public int event;
        public String path;
        public Changes(int event ,String path){
            this.event = event;
            this.path = path;
        }
    }



    public String serialize(int prev_snapID){

        if(prev_snapID == idAllocator.getFSId())
            return null;

        String result = gson.toJson(fsSnapshots.subList(prev_snapID + 1, fsSnapshots.size()));
        return result;
    }

    //TODO yet to be used on client side
    public void deserialize(String changes){

    }

    public void addLog(MyFile file,int event){
        int id = idAllocator.getFSId();
        //TODO check if there are active devices. If true then "id=idAllocator.incrementFSId()"
        Changes change = new Changes(event,file.path);
        Snapshot snap = fsSnapshots.get(id);
        snap.change.add(change);

    }

    //TODO
    // To be called when any pull request is released
    public void incrementID(){
        idAllocator.incrementFSId();
    }

}
