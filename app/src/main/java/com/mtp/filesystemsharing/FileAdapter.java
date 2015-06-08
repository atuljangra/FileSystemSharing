package com.mtp.filesystemsharing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.filesystemsharing.R;
import com.mtp.connection.manager.Device;
import com.mtp.fsmanager.external.ExternalFSManager;
import com.mtp.fsmanager.internal.MyFile;
import com.mtp.transmission.FSMessage;
import com.mtp.transmission.FTServer;
import com.mtp.transmission.FileServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vivek on 3/13/15.
 */
public class FileAdapter extends ArrayAdapter<MyFile> {

    Context mContext;

    MyFile currDir;
    public  ArrayList<ExternalFSManager> extFsManagers;
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
        extFsManagers = new ArrayList<>();

    }
    public synchronized  void  addExternalFS(ExternalFSManager fs){
        extFsManagers.add(fs);
        if(this.isRoot)
            this.add(fs.root);
    }

    public synchronized void removeExternalFS(ExternalFSManager fs){

    }

    public synchronized void changeDir(int position){
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
            if(baseDirInd == 0){
                MainActivity.fsManager.addToAdaptor(this,currDir);
            }else{
                extFsManagers.get(baseDirInd-1).addToAdaptor(this,currDir);
            }

        }else {
            if (baseDirInd != 0) {
                //TODO need to fecth file
                //start and asynch task and call openfile on completion
                FileServer fileServ = new FileServer(f.name,this);
                fileServ.start();

                Device dev = MainActivity.deviceManager.getDevice(extFsManagers.get(baseDirInd-1));

                FSMessage m = new FSMessage(FSMessage.REQUESTFILE, f.path);
                dev.conToClient.sendMessage(m);
                return;
            }
            try {
                openFile(new File(f.path));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public  void openFile( File url) throws IOException {
        // Create URI
        File file=url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/zip");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String s[] = {"video/*", "text/plain","image/jpeg"};
        intent.putExtra( Intent.EXTRA_MIME_TYPES,s);
        mContext.startActivity(intent);
    }

    public boolean toParent(){

        if(isRoot)
            return false;
        if(currDir.parent == null) {
            clear();
            isRoot = true;
            add(MainActivity.fsManager.root);
            MainActivity.deviceManager.addToAdaptor(this);
            return true;
        }
        currDir = currDir.parent;
        clear();
        addAll(currDir.child);
        return true;
    }

    public void refresh(){
        if(isRoot)
            return;


        clear();
        if(baseDirInd == 0){
            MainActivity.fsManager.addToAdaptor(this,currDir);
        }else{
            extFsManagers.get(baseDirInd-1).addToAdaptor(this,currDir);
        }
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
        if(isRoot & position > 0){
            Device dev = MainActivity.deviceManager.getDevice(extFsManagers.get(position-1));
            String name = dev.ip;
            String s[] = name.split("\\.");
            tv.setText(s[2]+"."+s[3]+":"+item.name);
        }
        else {
            tv.setText(item.name);
        }
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
