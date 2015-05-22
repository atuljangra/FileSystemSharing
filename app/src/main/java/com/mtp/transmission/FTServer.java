package com.mtp.transmission;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by vivek on 21/5/15.
 */
public class FTServer implements Serializable {
    public String ip;
    public int port;
    public String file;

    public String serialize(){
        Gson g = new Gson();
        return g.toJson(this);
    }
}
