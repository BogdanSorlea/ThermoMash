/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogdan
 */
public class BroadcastClient extends Thread {
    
    private MulticastSocket socket = null;
    private String message = null;
    private int transmissionRepeat = 0;
    
    BroadcastClient() {
        try {
            socket = new MulticastSocket(Settings.BROADCAST_PORT);
        } catch (IOException e) {
            
        }
    }
    
    public void transmitOnce(String data){
        message = data;
        transmissionRepeat = 1;
    }
    
    @Override
    public void run() {
        
        while (true) {
            
            if ( transmissionRepeat == 0 || message == null ) {
                message = null;
                continue;
            }
            
            transmissionRepeat--;
            
            byte[] buffer = message.getBytes();

            try {
                InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer,
                        buffer.length,
                        group,
                        Settings.BROADCAST_PORT);
                socket.send(packet);
                socket.close();
            } catch (UnknownHostException e) {

            } catch (IOException e) {
            
            }
        }
        
    }
    
}
