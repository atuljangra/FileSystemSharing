package com.mtp.fsmanager.internal;

import com.google.gson.Gson;
import com.mtp.transmission.FSMessage;
import com.mtp.transmission.MessageHandler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 30/4/15.
 */

//TODO  The size of snapshots can grow large. Need to flush or persist.
public class FSLogger {


    private FSIdAllocator idAllocator;

    private Gson gson;

    private ArrayList<Snapshot> fsSnapshots = new ArrayList<Snapshot>();

    public FSLogger() {
        idAllocator = new FSIdAllocator();
        gson = new Gson();
        Snapshot snap = new Snapshot();
        snap.snapshot_id = 0;
        fsSnapshots.add(snap);

    }


    public String serialize(int prev_snapID) {

        if (prev_snapID == idAllocator.getFSId())
            return null;


        String result = gson.toJson(fsSnapshots.subList(prev_snapID + 1, fsSnapshots.size()));
        return result;
    }

    //TODO yet to be used on client side
    public void deserialize(String changes) {

    }

    public void addLog(MyFile file, int event) {
        int id = idAllocator.getFSId();
        //TODO check if there are active devices. If true then "id=idAllocator.incrementFSId()"
        // Now there ar no active inactive devices so no need to increment
        Changes change = new Changes(event, file.path);
        Snapshot snap = fsSnapshots.get(id);
        snap.change.add(change);

        MessageHandler msgHandler = new MessageHandler();
        msgHandler.respond(new FSMessage(FSMessage.CHANGE, change.serialize()));

    }

    //TODO
    // To be called when any pull request is released
    public void incrementID() {
        idAllocator.incrementFSId();
    }

}
