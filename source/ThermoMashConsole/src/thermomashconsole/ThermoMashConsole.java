/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomashconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogdan
 */
public class ThermoMashConsole {

    private static String lastResponseIP = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        byte[] command = new byte[255];
        
        for (;;) {
            
            System.out.println("SELECT COMMAND:");
            System.out.println("1. QUERY TEMPERATURE");
            System.out.println("0. EXIT");
            
            String cmd = null;
 
            try{
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                cmd = bufferRead.readLine();

                //System.out.println(s);
            } catch(IOException e) {
                //e.printStackTrace();
            }
            
            if (cmd != null && cmd.equals("0"))
                break;
            
            if (cmd != null && !(cmd.equals("0") || cmd.equals("1")))
                continue;
            
            boolean notok = true;
            
            for (int i=0; i<5; i++) {
                
                transmitBroadcast(Settings.QUERYDATA);
                String response = null;
                response = receiveBroadcast(Settings.BROADCAST_RECEIVE_TIMEOUT);
                //System.out.print("REsponse: " + response);
            // If there is already an admin and a monitor in the system.
                if ( response != null 
                    && response.contains(Settings.DATARESPONSE) ) {
                    System.out.println("Temperature is: " + 
                            response.trim().replace(Settings.DATARESPONSE, ""));
                    notok = false;
                    break;
                } 
            }
            
            if (notok)
                System.out.println("NOTE: Admin not responsive.");
        }
        
    }
    
    private static void transmitBroadcast(String data){
        try {
            MulticastSocket socket = new MulticastSocket(Settings.BROADCAST_PORT);

            byte[] buffer = data.getBytes();

            InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length,
                    group,
                    Settings.BROADCAST_PORT);
            socket.send(packet);
            socket.close();
        } catch (IOException e){
        }
    }
    
    private static String receiveBroadcast(int timeout){
        String data = null;
        try {
            MulticastSocket socket = new MulticastSocket(Settings.BROADCAST_PORT);
            InetAddress group = InetAddress.getByName(Settings.BROADCAST_ADDRESS);
            socket.joinGroup(group);
            socket.setSoTimeout(timeout);
            DatagramPacket packet;
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            
            try {
                socket.receive(packet);
                data = new String(packet.getData());
                data = data.trim();
               
                lastResponseIP = packet.getSocketAddress().toString().trim();//socket.getRemoteSocketAddress().toString();
                lastResponseIP = lastResponseIP.replace("/", "");
                lastResponseIP = lastResponseIP.substring(0, lastResponseIP.indexOf(":"));
                //System.out.println("broadcast from: " + lastResponseIP);
            } catch (SocketTimeoutException e) {
                data = null;
                lastResponseIP = null;
            }
            
            socket.leaveGroup(group);
            socket.close();
        } catch (UnknownHostException ex) {
            //Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
    
    private static String getIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
        }
        return null;
    }
}
