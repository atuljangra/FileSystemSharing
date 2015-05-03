package com.mtp.fsmanager.internal;

import java.io.Serializable;

/**
 * Created by vivek on 30/4/15.
 */
public class Changes implements Serializable {

    public static transient int CREATED = 0x01;
    public static transient int DELETED = 0x02;
    public static transient int ISDIR = 0x04;

    public int event;
    public String path;

    public Changes(int event, String path) {
        this.event = event;
        this.path = path;
    }
}