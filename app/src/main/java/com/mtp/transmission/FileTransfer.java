package com.mtp.transmission;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by vivek on 20/5/15.
 */
public class FileTransfer implements Serializable {
    public String name;
    public String data;
    public String serialize(){
        Gson g = new Gson();
        return g.toJson(this);
    }
}
