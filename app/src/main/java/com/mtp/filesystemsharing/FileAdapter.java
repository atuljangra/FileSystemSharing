package com.mtp.filesystemsharing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.filesystemsharing.R;
import com.mtp.fsmanager.external.ExternalFSManager;
import com.mtp.fsmanager.internal.MyFile;

/**
 * Created by vivek on 3/13/15.
 */
public class FileAdapter extends ArrayAdapter<MyFile> {

    Context mContext;

    MyFile currDir;

    public Boolean isRoot = true;
    int baseDirInd = 0;

    public FileAdapter(Context c, int resource, MyFile[] x){
        super(c,resource,x);
        mContext = c;
    }

    public FileAdapter(Context c, int resource){
        super(c,resource);
        mContext = c;
    }

    public FileAdapter(Context c, int resource, int textId){
        super(c,resource, textId);
        currDir = MainActivity.fsManager.root;
        mContext = c;

    }

    public void changeDir(int position){
        if(isRoot) {
            baseDirInd = position;
            Log.d("entering",Integer.toString(position));
        }
        isRoot = false;
        MyFile f = getItem(position);
        Log.d("enter dir", f.name);
        if(f.isDirectory){
            currDir = f;
            clear();
            addAll(currDir.child);
        }
    }

    public boolean toParent(){

        if(isRoot)
            return false;
        if(currDir.parent == null) {
            clear();
            isRoot = true;
            add(MainActivity.fsManager.root);
            for(ExternalFSManager ex:MainActivity.extFsManagers){
                add(ex.root);
            }
            return true;
        }
        currDir = currDir.parent;
        clear();
        addAll(currDir.child);
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            //Inflate the layout
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.grid_item, null);

            // Add The Text!!!

        }
        MyFile item = getItem(position);
        TextView tv = (TextView)v.findViewById(R.id.grid_text);
        tv.setText(item.name);
//        /tv.setHeight(10);
        // Add The Image!!!

        ImageView iv = (ImageView)v.findViewById(R.id.grid_image);
        if(item.isDirectory)
            iv.setImageResource(mThumbIds[0]);
        else
            iv.setImageResource(mThumbIds[1]);
        iv.setMaxHeight(10);
        iv.setMaxWidth(10);
        //iv.setLayoutParams(new GridView.LayoutParams(85, 85));
        //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //v.setPadding(8, 8, 8, 8);
        return v;
    }

    public static Integer[] mThumbIds = {
        R.drawable.f1,R.drawable.f4
    };


}
