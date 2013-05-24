/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author Bogdan
 */
public class MulticastServerThread extends Thread {
    
    protected MulticastSocket socket;
    private String data;
    
    public MulticastServerThread(String data) throws IOException {
        super();
        this.socket = null;
        this.socket = new MulticastSocket(Settings.BROADCAST_PORT);
        this.data = data;
    }
    
    // see http://www.nakov.com/inetjava/lectures/part-1-sockets/InetJava-1.5-UDP-and-Multicast-Sockets.html
    
    @Override
    public void run() {
        try { 

            byte[] buffer;
            buffer = this.data.getBytes();

            InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length,
                    group,
                    Settings.BROADCAST_PORT);
            socket.send(packet);

//                try {
//                    sleep((long) Settings.BROADCAST_RESEND_INTERVAL);
//                } catch (InterruptedException e) {
//                }

        } catch (IOException e){
        }
        socket.close();
    }
    
}
