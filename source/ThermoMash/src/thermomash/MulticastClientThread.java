/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogdan
 */
public class MulticastClientThread extends Thread{
    
    private String data;
    private MulticastSocket socket;
    
    MulticastClientThread() throws IOException{
        super();
        this.socket = null;
        this.socket = new MulticastSocket(Settings.BROADCAST_PORT);
    }
    
    public String getData(){
        return this.data;
    }
    
    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
            socket.joinGroup(group);
     
            DatagramPacket packet;
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            data = new String(packet.getData());
            socket.leaveGroup(group);
            socket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}