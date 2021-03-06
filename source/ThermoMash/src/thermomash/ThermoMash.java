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


/**
 *
 * @author Bogdan
 */
public class ThermoMash {

    private static int NETWORK_ATTACH_ATTEMPTS = 0;
    private static int MAX_NETWORK_ATTACH_ATTEMPTS = 5;
     
    private static boolean IS_ADMIN = false;
    private static boolean IS_MONITOR = false;
    private static boolean IS_WORKER = false;
    
    private static String lastResponseIP = null;
    private static String adminIP = null;
    private static String monitorIP = null;
    
    private static Map<String, Integer> data = new HashMap<>();
    private static Map<String, Integer> ids = new HashMap<>();
    private static int noOfNodes = 0;
    
    private static boolean adminOkSent = false;
    private static boolean monitorOkSent = false;
    private static int adminOkIterations = 0;
    private static int monitorOkIterations = 0;
    private static int workerLoopCount = 0;
    private static int iteration = 0;
    
    private static int[] values = new int[255];
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Attempting NETWORK CONNECT...");
        
        while (NETWORK_ATTACH_ATTEMPTS < MAX_NETWORK_ATTACH_ATTEMPTS) {
            System.out.print("\tA" + NETWORK_ATTACH_ATTEMPTS);
                    //+ " (" + getIP() + "): ");
            
            transmitBroadcast(Settings.NETWORK_ATTACH_REQ);
            String response = null;
            response = receiveBroadcast(Settings.BROADCAST_RECEIVE_TIMEOUT);
            System.out.print("REsponse: " + response);
            // If there is already an admin and a monitor in the system.
            if ( response != null 
                    && response.contains(Settings.NETWORK_ATTACH_CONF)
                    && response.contains(getIP()) ) {
                IS_ADMIN = false;
                IS_WORKER = true;
                adminIP = lastResponseIP;
                System.out.print("RESP. FROM " + adminIP);
                break;
            } 
            // If there is already an admin in the system.
            else if ( response != null 
                    && response.contains(Settings.NETWORK_ATTACH_MONITOR)
                    && response.contains(getIP()) ) {
                IS_ADMIN = false;
                IS_WORKER = true;
                IS_MONITOR = true;
                adminIP = lastResponseIP;
                System.out.print("RESP. FROM " +  adminIP);
                break;
            }
            
            IS_ADMIN = true;
            IS_WORKER = false;
            NETWORK_ATTACH_ATTEMPTS++;
            if ( NETWORK_ATTACH_ATTEMPTS == MAX_NETWORK_ATTACH_ATTEMPTS ) {
                System.out.println("NO RESPONSE. UPGRADING TO ADMIN.");
                noOfNodes++;
                ids.put(getIP(), noOfNodes);
            }
                
        }
        
        while (true) {

            if ( iteration == noOfNodes - 2){
                iteration = 0;
            } else {
                iteration++;
            }
            
            // readphase
            String request = null;
            request = receiveBroadcast(Settings.BROADCAST_FAST_RECEIVE_TIMEOUT);
            
            // writephase
            if ( IS_ADMIN ){
                System.out.println("ADM. Conn. nodes: " + ids.size());
                
                data.put(getIP(), (int) Math.round(20 + Math.random() * 10));
                
                if ( request != null ){
                if ( request.equals(Settings.NETWORK_ATTACH_REQ) ){
                    System.out.println("NATTREQ");
                    if ( ids.get(lastResponseIP) == null ) {
                        noOfNodes++;
                        ids.put(lastResponseIP, noOfNodes);
                    }
                    if ( noOfNodes == 2 ) {
                        transmitBroadcast(lastResponseIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.NETWORK_ATTACH_MONITOR);
                        System.out.println(lastResponseIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.NETWORK_ATTACH_MONITOR);
                        monitorIP = lastResponseIP;
                    } else {
                        transmitBroadcast(lastResponseIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.NETWORK_ATTACH_CONF);
                        System.out.println(lastResponseIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.NETWORK_ATTACH_CONF);
                    }
                    
                } else if ( request.equals(Settings.ADMIN_RUNNING) ) {
                        transmitBroadcast(lastResponseIP
                                    + Settings.FIELD_DELIMITER 
                                    + getIP() + Settings.FIELD_DELIMITER
                                    + Settings.ADMINOK);
                        
                } else if ( request.equals(Settings.QUERYDATA) ) {
                        long sum = 0;
                        int count = 0;
                        
                        for (String key : data.keySet()) {
                            sum += data.get(key);
                            count++;
                            //System.out.println(" -- " + key + " :: " + data.get(key));
                        }
                        
                        for (int i=0; i<noOfNodes-1; i++) {
                            sum += values[i];
                        }
                        sum += (int) Math.round(20 + Math.random() * 10);
                        
                        if ( noOfNodes > 0 )
                            transmitBroadcast(Settings.DATARESPONSE + 
                                    (int) sum / noOfNodes);
                        else
                            transmitBroadcast(Settings.DATARESPONSE +
                                    "NOT AVAILABLE");
                } else {

                    if ( request.contains(Settings.MONITOROK) ) {
                        monitorOkIterations = 0;
                        monitorOkSent = false;
                    }
                    if ( request.contains(Settings.DATAPREFIX) ) {
                        System.out.println("HERE... " + request);
                        
                        values[iteration] = Integer.parseInt(
                                    request.split(Settings.FIELD_DELIMITER)[2]
                                        .replace(Settings.DATAPREFIX, "")
                                        .trim());
                        
                        data.put(lastResponseIP, 
                                Integer.parseInt(
                                    request.split(Settings.FIELD_DELIMITER)[2]
                                        .replace(Settings.DATAPREFIX, "")
                                        .trim()));
                    }

                    if ( monitorOkIterations == Settings.MONITOR_RESPONSE_ITERATIONS ) {
                        System.out.println("!!! NEED TO SPAWN A NEW MONITOR !!!");
                        monitorOkSent = false;
                    }

                    if ( !monitorOkSent ){
                        transmitBroadcast(monitorIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.MONITOR_RUNNING);
                        monitorOkSent = true;
                        monitorOkIterations = 0;
                    }    
                }
                
                }
            }
            
            if ( IS_MONITOR ){
                System.out.println("MONIT.");
                if ( request != null )
                if ( request.contains(Settings.MONITOR_RUNNING) ) {
                    transmitBroadcast(adminIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.MONITOROK);
                } else {
                    if ( request.contains(Settings.ADMINOK) ) {
                        adminOkIterations = 0;
                        adminOkSent = false;
                    }
                    if ( request.contains(Settings.DATAPREFIX) ) {
                        data.put(lastResponseIP, 
                                Integer.parseInt(
                                    request.split(Settings.FIELD_DELIMITER)[2]
                                        .replace(Settings.DATAPREFIX, "")
                                        .trim()));
                    }
                
                    if ( adminOkIterations == Settings.ADMIN_RESPONSE_ITERATIONS ) {
                        System.out.println("!!! NEED TO SPAWN A NEW ADMIN !!!");
                        adminOkSent = false;
                    }
                
                    if ( !adminOkSent ){
                        transmitBroadcast(adminIP
                                + Settings.FIELD_DELIMITER
                                + getIP() + Settings.FIELD_DELIMITER
                                + Settings.ADMIN_RUNNING);
                        adminOkSent = true;
                        adminOkIterations = 0;
                    }
                    
                }
            }
            
            
            
            if ( IS_WORKER ){
                if (workerLoopCount == 15){
                    System.out.print("WORK: ");
                    String dat = adminIP
                                    + Settings.FIELD_DELIMITER
                                    + getIP() + Settings.FIELD_DELIMITER
                                    + Settings.DATAPREFIX + Long.toString(
                                Math.round(20 + Math.random() * 10));
                    System.out.println(dat);
                    transmitBroadcast(dat);
                    
                    workerLoopCount = 0;
                } else {
                    workerLoopCount++;
                }
            }
            
            
            
            if (IS_MONITOR)
                adminOkIterations++;
            if (IS_ADMIN)
                monitorOkIterations++;
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
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MulticastClientThread.class.getName()).log(Level.SEVERE, null, ex);
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


