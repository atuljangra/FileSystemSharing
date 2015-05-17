package com.mtp.fsmanager.external;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mtp.fsmanager.internal.Changes;
import com.mtp.fsmanager.internal.FSLogger;
import com.mtp.fsmanager.internal.MyFile;
import com.mtp.fsmanager.internal.Snapshot;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vivek on 30/4/15.
 * deserializes to external fs and maintains the changes done
 */
public class ExternalFSManager {
    Gson gson;
    public MyFile root;

    public ExternalFSManager(String fs) {
        gson = new Gson();
        root = gson.fromJson(fs, MyFile.class);
    }

    /*Changes cannot be directly done to the root dir, so parent cannot be null*/
    public void logChanges(String log) {
        Type collectionType = new TypeToken<Collection<Snapshot>>() {
        }.getType();
        ArrayList<Snapshot> fsSnapshots = gson.fromJson(log, collectionType);
        for (Snapshot snap : fsSnapshots) {
            for (Changes cha : snap.change) {
                applyChange(cha);
            }
        }
    }

    private void applyChange(Changes change) {
        File path = new File(change.path);
        String filename = path.getName();
        MyFile parent = getCorrParent(path, this.root);
        assert parent != null;
        if ((change.event & Changes.CREATED) > 0) {
            MyFile f = new MyFile();
            f.isDirectory = (change.event & Changes.ISDIR) > 0 ? true : false;
            f.name = filename;
            f.path = change.path;
            parent.child.add(f);
        } else if ((change.event & Changes.DELETED) > 0) {
            MyFile child = search(parent, filename);
            assert child != null;
            parent.child.remove(child);

        } else {
            throw new IllegalArgumentException("unexpected log event");
        }
    }

    private MyFile getCorrParent(File path, MyFile root) {
        File childList[] = path.listFiles();
        assert childList.length > 0;
        if (childList[0].list().length == 0)
            return root;


        File child = childList[0];
        for (MyFile corrChild : root.child) {
            if (corrChild.path.equals(child.getPath())) {
                return getCorrParent(child, corrChild);
            }
        }
        return null;//getCorrParent()
    }

    private MyFile search(MyFile root, String file) {
        for (MyFile child : root.child) {
            if (child.name.equals(file)) {
                return child;
            }
        }
        return null;
    }

    /* The fs received doesn't have parent node setup. so we need to establish that*/
    public void establishRelation(MyFile child, MyFile parent){
        child.parent = parent;
        for(MyFile f :child.child){
            establishRelation(f,child);
        }
    }
    /*private File getRoot(File child){
        if(child.getParentFile() == null)
            return child;
        return getRoot(child.getParentFile());
    }*/
    /*private MyFile locateFile(MyFile root, File path, String file){
        File[] childList = path.listFiles();
        assert childList.length < 2;
        if(childList.length == 0){

        }

        return root;
    }*/
}
