/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import tcp.server.comm.HandleServerTCP;		// The communication handler
import tcp.client.comm.HandleClientTCP;		// The communication handler

/**
 *
 * @author Bogdan
 */
public class ThermoMash {

    private static int NETWORK_ATTACH_ATTEMPTS = 0;
    private static int MAX_NETWORK_ATTACH_ATTEMPTS = 5;
     
    private static boolean IS_ADMIN = false;
    private static boolean IS_MONITOR = false;
    private static boolean IS_WORKER = true;
    
    private static String lastResponseIP = null;
    private static String adminIP = null;
    private static String monitorIP = null;
    
    private static Map<String, Integer> data = new HashMap<>();
    private static int noOfNodes = 0;
    
    private static boolean adminOkSent = false;
    private static boolean monitorOkSent = false;
    private static int adminOkIterations = 0;
    private static int monitorOkIterations = 0;
   
    private static boolean jj = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Attempting NETWORK CONNECT...");
        
        while (NETWORK_ATTACH_ATTEMPTS < MAX_NETWORK_ATTACH_ATTEMPTS) {
            System.out.print("\tAttempt " + NETWORK_ATTACH_ATTEMPTS + ": ");
//            try {
//                new MulticastServerThread("NETWORK ATTACH REQUEST").start();
//                new MulticastClientThread().start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            
            transmitBroadcast(Settings.NETWORK_ATTACH_REQ);
            String response = null;
            //response = receiveBroadcast(Settings.BROADCAST_RECEIVE_TIMEOUT);

            HandleServerTCP connection = new HandleServerTCP();  
                adminIP = connection.getIPAddress();
                connection.openSocket(adminIP,Settings.ADMIN_PORT);
                response = connection.readCommand();
            
            // If there is already an admin and a monitor in the system.
            if ( response != null 
                    && response.equals(Settings.NETWORK_ATTACH_CONF) ) {
                IS_ADMIN = false;
                IS_WORKER = true;                
                System.out.print("RESP. FROM " + lastResponseIP);
                break;
            } 
            // If there is already an admin in the system.
            else if ( response != null 
                    && response.equals(Settings.NETWORK_ATTACH_MONITOR) ) {
                IS_ADMIN = false;
                IS_WORKER = true;
                IS_MONITOR = true;
                  //Client address
                System.out.print("RESP. FROM " +  lastResponseIP);
                break;
            }
            
            IS_ADMIN = true;
            noOfNodes = 1;
            NETWORK_ATTACH_ATTEMPTS++;
            System.out.println("NO RESPONSE. UPGRADING TO ADMIN.");
        }
        
        while (true) {
            /*
            // readphase
            String request = null;
            request = receiveBroadcast(Settings.BROADCAST_FAST_RECEIVE_TIMEOUT);
            System.out.println(request);
            
            // writephase
            if ( IS_ADMIN ){
                System.out.println("I am an Admin");
                System.out.println("the number of connected nodes: " + noOfNodes);
                
                if (jj == false){
                // Create the Handle Connection object
                HandleServerTCP connection = new HandleServerTCP();  
                adminIP = connection.getIPAddress();
                connection.openSocket(adminIP,Settings.ADMIN_PORT);	
                System.out.println("Admins IP is: " + adminIP);
                jj = true;
                
                
                }
                
                if ( request != null ){
                if ( request.equals(Settings.NETWORK_ATTACH_REQ) ){

                    if ( noOfNodes == 1 ) {
                        transmitBroadcast(Settings.NETWORK_ATTACH_MONITOR);
                    } else {
                        transmitBroadcast(Settings.NETWORK_ATTACH_CONF);
                    }
                    noOfNodes++;
                } else if ( request.equals(Settings.ADMIN_RUNNING) ) {
                    transmitBroadcast(Settings.ADMINOK);
                    adminOkIterations = 0;
                    adminOkSent = false;
                } else {
                    if ( request.contains(Settings.DATAPREFIX) ) {
                        data.put(lastResponseIP, 
                                Integer.parseInt(
                                    request.replace(Settings.DATAPREFIX, "")
                                        .trim()));
                    }
                    if ( !monitorOkSent ){
                        transmitBroadcast(Settings.MONITOR_RUNNING);
                        monitorOkSent = true;
                        monitorOkIterations = 0;
                    }// else if ( monitorOkSent && monitorOkIterations > 20 ){
                        // upgrade a regular instance to monitor
                    //}      
                }}
            }
            
            if ( IS_MONITOR ){
                System.out.println("I AM MONITOR.");
                if ( request != null )
                if ( request.equals(Settings.NETWORK_ATTACH_REQ) ){
                    transmitBroadcast(Settings.NOTIFY_MONITOR);
                } else if ( request.equals(Settings.MONITOR_RUNNING) ) {
                    transmitBroadcast(Settings.MONITOROK);
                    monitorOkIterations = 0;
                    monitorOkSent = false;
                }
                
                if ( !adminOkSent ){
                    transmitBroadcast(Settings.ADMIN_RUNNING);
                    adminOkSent = true;
                    adminOkIterations = 0;
                }
            }
            
            if ( IS_WORKER ){
            System.out.println("I am a Worker");
            
            //if (adminIP!= null){            
             //   HandleClientTCP client = new HandleClientTCP();
                // Connect to the server
              //  client.connectToServer(adminIP,Settings.ADMIN_PORT);}
            
                //transmitBroadcast(Settings.DATAPREFIX + Long.toString(
                //            Math.round(17 + Math.random() * 10)));
            }
            
            adminOkIterations++;
            monitorOkIterations++;
            */ 
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
               
                lastResponseIP =  packet.getSocketAddress().toString();//socket.getRemoteSocketAddress().toString();
                System.out.println("broadcast from: " + lastResponseIP);
            } catch (SocketTimeoutException e) {
                data = null;
                lastResponseIP = null;
            }
            
            socket.leaveGroup(group);
            socket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
}
