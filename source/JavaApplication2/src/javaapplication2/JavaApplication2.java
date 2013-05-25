/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Bogdan
 */
public class JavaApplication2 {

    //private String lastResponseIP;
    
    private static boolean IS_ADMIN = true;
    private static boolean IS_WORKER = false;
    private static boolean IS_MONITOR = false;
    private static int noOfNodes = 0;
    private static String lastResponseIP = null;
    private static String adminIP = null;
    private static String broadcastIn = null;
    
    private static BroadcastClient broadcastWriter = null;
    private static BroadcastServer broadcastReader = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        broadcastWriter = new BroadcastClient();
        broadcastWriter.start();
        
        broadcastReader = new BroadcastServer();
        broadcastReader.start();
        
        for (int i=0; i<5; i++) {
            System.out.print(i);
            broadcastWriter.transmitOnce(Settings.NETWORK_ATTACH_REQ);
            broadcastReader.setTimeout(Settings.BROADCAST_RECEIVE_TIMEOUT);
            
            
            while ( broadcastReader.CONTINUE_RUN ){
                //System.out.print(".");
            }
            
            if ( broadcastReader.message != null && 
                    broadcastReader.message.equals(Settings.NETWORK_ATTACH_CONFIRM)) {
                IS_WORKER = true;
                IS_ADMIN = false;
                IS_MONITOR = false;
                break;
            }
            
            broadcastReader.CONTINUE_RUN = true;
            
        }
        
        broadcastReader.setTimeout(0);
        
        if (IS_ADMIN)
            System.out.println("ADMIN!");
        if (IS_WORKER)
            System.out.println("WORKER!");
        if (IS_MONITOR)
            System.out.println("MONITOR!");
        
        System.out.print("  MY IP IS: " + getIP());
        
        while (true) {
            
            if (IS_ADMIN)
                if ( broadcastReader.message != null ) {
                    if ( broadcastReader.message.contains(Settings.NETWORK_ATTACH_REQ)) {
                        broadcastWriter.transmitOnce(Settings.NETWORK_ATTACH_CONFIRM);
                    }
                }
                    
                    
                    
            broadcastReader.CONTINUE_RUN = true;
            
        }
        
    }
    
    private static String getIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
        }
        return null;
    }
}
