package com.mtp.fsmanager.internal;

import android.util.Log;

import com.google.gson.Gson;
import com.mtp.filesystemsharing.MainActivity;
import com.mtp.transmission.FSMessage;
import com.mtp.transmission.MessageHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivek on 30/4/15.
 */

//TODO  The size of snapshots can grow large. Need to flush or persist.
public class FSLogger {


    private FSIdAllocator idAllocator;

    private Gson gson;

    private ArrayList<Snapshot> fsSnapshots = new ArrayList<Snapshot>();

    private boolean activeInLastUpdate = false;
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

        List<Snapshot> l = fsSnapshots.subList(prev_snapID + 1, fsSnapshots.size());
        String result = gson.toJson(l);
        Log.d("logger serialization", result);
        return result;
    }

    //TODO yet to be used on client side
    public void deserialize(String changes) {

    }

    public void addLog(MyFile file, int event) {
        int id = idAllocator.getFSId();
        Changes change = new Changes(event, file.path);
        if(!MainActivity.deviceManager.activeDevicesPresent()){
            // Now there ar no active inactive devices so no need to increment
            Snapshot snap = fsSnapshots.get(id);
            if(activeInLastUpdate) {
                id = idAllocator.incrementFSId();
                snap = new Snapshot();
                snap.snapshot_id = id;
                fsSnapshots.add(snap);
            }
            activeInLastUpdate = false;

            snap.change.add(change);

        }
        else {

            //TODO check if there are active devices. If true then "id=idAllocator.incrementFSId()"
            id = idAllocator.incrementFSId();
            activeInLastUpdate = true;
            Snapshot snap = new Snapshot();
            snap.snapshot_id = id;
            fsSnapshots.add(snap);
            snap.change.add(change);
            MainActivity.deviceManager.sendUpdates(new FSMessage(FSMessage.CHANGES, serialize(id-1)));

        }


    }

    //TODO
    // To be called when any pull request is released
    public void incrementID() {
        idAllocator.incrementFSId();
    }

}
