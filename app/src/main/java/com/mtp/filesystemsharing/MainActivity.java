package com.mtp.filesystemsharing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vivek.filesystemsharing.R;
import com.mtp.connection.manager.Device;
import com.mtp.connection.manager.DeviceManager;
import com.mtp.connection.manager.client.ClientConnectionManager;
import com.mtp.connection.manager.client.ClientListener;
import com.mtp.connection.manager.server.ServerListener;
import com.mtp.connection.manager.service.discovery.GetService;
import com.mtp.connection.manager.service.discovery.RegisterService;
import com.mtp.connection.manager.service.discovery.ServiceSocket;
import com.mtp.fsmanager.internal.FSService;
import com.mtp.fsmanager.internal.LocalFSManager;


public class MainActivity extends Activity {

    private GetService serviceReceiver = null;
    private RegisterService serviceBroadcaster = null;
    private DeviceManager deviceManager = null;
    private ServiceSocket servSocket = null;
    private ArrayAdapter<Device> deviceAdaptor;
    private ServerListener server;
    public static LocalFSManager fsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fsManager = new LocalFSManager();
        startService(new Intent(getApplicationContext(), FSService.class));

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void serviceBroadcast(View view){
        if(servSocket == null){
            servSocket = new ServiceSocket();
        }
        if(serviceBroadcaster != null)
            return;

        serviceBroadcaster = new RegisterService(this, servSocket);
        serviceBroadcaster.start();

        server = new ServerListener(this,fsManager);
        server.start();
    }

    public void getService(View view){
        if(servSocket == null){
            servSocket = new ServiceSocket();
        }
        if(serviceReceiver != null)
            return;

        deviceManager = new DeviceManager(this);
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

                serv.conToServer = new ClientConnectionManager(serv.ip);
                serv.conToServer.startListening();

            }
        });

        serviceReceiver = new GetService(this, servSocket, deviceManager);
        serviceReceiver.start();
    }

}
