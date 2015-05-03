package com.mtp.fsmanager.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vivek on 29/4/15.
 * This is the allocator of
 */
public class FSIdAllocator {
    private static AtomicInteger fsID = new AtomicInteger(0);

    public int getFSId() {
        return fsID.get();
    }

    public int incrementFSId() {
        //Todo check for overflow and do something about it
        return fsID.incrementAndGet();
    }

}
