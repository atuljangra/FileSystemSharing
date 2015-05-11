package com.mtp.connection.manager.client;

import com.mtp.connection.manager.server.ServerListener;
import com.mtp.transmission.Message;
import com.mtp.transmission.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by vivek on 3/5/15. Establishes connection to the server.
 * Listens to the incoming messages.
 */
//TODO create message sender
public class ClientListener extends Thread{

    private boolean running = true;
    private String response = "";
    Socket socket;
    private  MessageHandler msgHandler ;
   /* public ClientListener(Socket socket){
        this.socket = socket;
        msgHandler = new MessageHandler();
    }
*/
    String serverIP;
   public ClientListener(String ip){
       this.serverIP = ip;
       msgHandler = new MessageHandler();
   }
    @Override
    public void run(){
        while(running) {
            try {
                //TODO Need to save the Listener with the serve
                socket = new Socket(serverIP, ServerListener.SocketServerPORT);
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

				/*
				 * notice:
				 * inputStream.read() will block if no data return
				 */

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    String packet = byteArrayOutputStream.toString("UTF-8");
                    int end = Message.searchEOM(packet);
                    response += packet;
                    String txtMsg;
                    if(end != -1){
                        end = response.length() - packet.length() + end;
                        txtMsg = response.substring(0, end);
                        response = Message.getRemainingMsg(response, end);
                        msgHandler.respond(txtMsg);
                    }
                    //TODO need to identify end og message.
                    //TODO Need to add message handlers.
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* to clear the resources */
    public void kill(){
        running = false;

    }
}
