package com.mtp.fsmanager;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vivek on 29/4/15.
 * This is the allocator of
 */
public class FSIdAllocator {
    private static AtomicLong fsID = new AtomicLong(0);

    public long getFSId(){
        return fsID.get();
    }

    public long incrementFSId(){
        //Todo check for overflow and do something about it
        return fsID.incrementAndGet();
    }

}
