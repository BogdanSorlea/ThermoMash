/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogdan
 */
public class BroadcastServer extends Thread {
    
    private MulticastSocket socket = null;
    public String message = null;
    public String ip = null;
    private int timeout;
    
    public boolean RECEIVED_BROADCAST = false;
    public boolean BROADCAST_TIMEOUT = false;
    
    public boolean CONTINUE_RUN = true;
    
    BroadcastServer() {
        
        createNewSocket();
        
    }
    
    private void createNewSocket(){
        try {
            socket = new MulticastSocket(Settings.BROADCAST_PORT);
            InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
            socket.joinGroup(group);
        } catch (IOException e) {
        }
    }
    
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    
    @Override
    public void run() {

        while (true) {
            
            if ( !CONTINUE_RUN )
                continue;
            
            try {
                if ( timeout > 0 )
                    socket.setSoTimeout(timeout);
                else
                    createNewSocket();
            } catch (SocketException e) {
                
            }
            
            DatagramPacket packet;
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            
            try {
                socket.receive(packet);
                message = new String(packet.getData()).trim();
                ip = packet.getSocketAddress().toString();//socket.getRemoteSocketAddress().toString();
                
                CONTINUE_RUN = false;
                
                System.out.println("DATA!" + message);
                //System.out.println("broadcast from: " + lastResponseIP);
            } catch (SocketTimeoutException e) {
                
                CONTINUE_RUN = false;
            
                System.out.println("TIMEOUT!");
            } catch (IOException ex) {
                
            }
        }
        
    }
    
    
    
}
