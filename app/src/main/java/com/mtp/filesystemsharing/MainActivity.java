package com.mtp.filesystemsharing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.provider.Settings;
import android.widget.Toast;

import com.example.vivek.filesystemsharing.R;
import com.mtp.connection.manager.Device;
import com.mtp.connection.manager.DeviceManager;
import com.mtp.connection.manager.Security.Auth;
import com.mtp.connection.manager.client.ClientConnectionManager;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.ServerListener;
import com.mtp.connection.manager.service.discovery.GetService;
import com.mtp.connection.manager.service.discovery.RegisterService;
import com.mtp.connection.manager.service.discovery.ServiceSocket;
import com.mtp.fsmanager.external.ExternalFSManager;
import com.mtp.fsmanager.internal.FSService;
import com.mtp.fsmanager.internal.LocalFSManager;


import java.util.ArrayList;


public class MainActivity extends Activity {

    private GetService serviceReceiver = null;
    private RegisterService serviceBroadcaster = null;

    private ServiceSocket servSocket = null;
    private ArrayAdapter<Device> deviceAdaptor;
    private ServerListener server;

    //TODO need to initialize it in async task and block options selection till it is done
    public static LocalFSManager fsManager;
    public static FileAdapter fileAdapter;
    public static DeviceManager deviceManager = null;
    public static Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fsManager = new LocalFSManager();
        fsManager.initializeLocalFS();
        deviceManager = new DeviceManager(this);
        auth = new Auth(this);

        startService(new Intent(getApplicationContext(), FSService.class));

        final GridView gridView = (GridView) findViewById(R.id.gridview);
        fileAdapter = new FileAdapter(this,R.layout.grid_item,R.id.grid_text);
        fileAdapter.add(fsManager.root);
        gridView.setAdapter(fileAdapter);
        servSocket = new ServiceSocket();
        Log.d("dcim", Environment.DIRECTORY_DCIM);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent = new Intent(getApplicationContext(), SingleView.class);
                intent.putExtra("id",position);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);*/
                Toast.makeText(getApplicationContext(),"Opening " + position,Toast.LENGTH_SHORT).show();
                //f.clear();
                fileAdapter.changeDir(position);
            }
        });

    }

    @Override
    public void onBackPressed(){
        Log.d("back ", "pressed");
        if(!fileAdapter.toParent()){

            Toast.makeText(getApplicationContext(),"Root Directory!",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void serviceBroadcast(View view){
        /*if(servSocket == null){
            servSocket = new ServiceSocket();
        }*/
        if(serviceBroadcaster != null)
            return;

        serviceBroadcaster = new RegisterService(this, servSocket);
        serviceBroadcaster.start();

        server = new ServerListener(this);
        server.start();
    }

    public void getService(View view){
        /*if(servSocket == null){
            servSocket = new ServiceSocket();
        }*/
        if(serviceReceiver != null)
            return;


        deviceAdaptor = new DeviceAdaptor(this, R.layout.devicelist, deviceManager.getList() );
        deviceManager.setAdap(deviceAdaptor);
        ListView list = (ListView)findViewById(R.id.listView);
        list.setAdapter(deviceAdaptor);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Device serv = deviceManager.getList().get(position);
                Toast.makeText(getApplicationContext(), "Trying to connect to " +
                        serv.ip, Toast.LENGTH_SHORT).show();

                // TODO Add authentication here.
               // auth.authenticate();
                serv.conToClient = new ClientConnectionManager(serv.ip);
                serv.conToClient.startListening();

            }
        });

        serviceReceiver = new GetService(this, servSocket, deviceManager);
        serviceReceiver.start();
    }

}
