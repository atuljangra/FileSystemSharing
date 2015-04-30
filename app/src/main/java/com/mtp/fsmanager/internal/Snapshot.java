package com.mtp.fsmanager.internal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivek on 30/4/15.
 */

public class Snapshot implements Serializable {
    public int snapshot_id;
    public ArrayList<Changes> change = new ArrayList<Changes>();
}

