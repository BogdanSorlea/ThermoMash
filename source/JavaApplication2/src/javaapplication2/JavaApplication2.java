/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ServerSocket server = null;
        Socket clientSocket = null;
        
        try {
            server = new ServerSocket(Settings.TCP_PORT);
        } catch (IOException ex) {
            //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("INIT: cannot create serversocket");
        }
        
        for (int i=0; i<5; i++){
            
            transmitBroadcast(Settings.NETWORK_ATTACH_REQ);
            String response = null;
            
            //response = receiveBroadcast(Settings.BROADCAST_RECEIVE_TIMEOUT);
            
            try {
                server.setSoTimeout(Settings.TCP_RECEIVE_TIMEOUT);
                clientSocket = server.accept();
                BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //PrintStream os = new PrintStream(clientSocket.getOutputStream());
                
                response = is.readLine();
                lastResponseIP = clientSocket.getRemoteSocketAddress().toString();
                
                //System.out.print("ATTACHLOOP: got the following TCP resp... " + response);
                is.close();
                clientSocket.close();
            } catch (IOException ex) {
                //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("ATTACHLOOP: IOException - probably socket timeout");
            }
            System.out.println("!!! " + response);
            if ( response != null
                    && response.contains(Settings.NETWORK_ATTACH_CONFIRM) ) {
                System.out.println("ATTACHLOOP: Simple attach confirmed.");
                IS_WORKER = true;
                IS_ADMIN = false;     
                lastResponseIP = lastResponseIP.trim().replace("/", "");
                lastResponseIP = lastResponseIP.substring(0, lastResponseIP.indexOf(":"));
                adminIP = lastResponseIP;
                break;
            }
            if ( response != null
                    && response.contains(Settings.NETWORK_ATTACH_MONITOR) ) {
                System.out.println("ATTACHLOOP: Monitor attach confirmed.");
                IS_WORKER = true;
                IS_ADMIN = false;
                IS_MONITOR = true;
                lastResponseIP = lastResponseIP.trim().replace("/", "");
                lastResponseIP = lastResponseIP.substring(0, lastResponseIP.indexOf(":"));
                adminIP = lastResponseIP;
                break;
            }
        }
        
        if (IS_WORKER)
            System.out.println("IS WORKER!");
        else if (IS_ADMIN)
            System.out.println("IS ADMIN!");
        else if (IS_MONITOR)
            System.out.print("IS MONITOR!");
        
        if (IS_ADMIN)
            System.out.print("  MY IP IS: " + getIP());
        else
            System.out.print("  ADMIN IP IS: " + adminIP);
        
//        ServerSocket servsocket = null;
//        Socket socket = null;
        
        if (IS_ADMIN) {
            noOfNodes = 1;
//            try {
//                servsocket = new ServerSocket(Settings.TCP_PORT);
//            } catch (IOException ex) {
//                System.exit(-1);
//            }
            
        }
        
        while(true){
            
            String request = null;
            request = receiveBroadcast(Settings.BROADCAST_FAST_RECEIVE_TIMEOUT);
            
            String response = null;
            try {
                server.setSoTimeout(Settings.TCP_RECEIVE_TIMEOUT);
                clientSocket = server.accept();
                BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //PrintStream os = new PrintStream(clientSocket.getOutputStream());

                response = is.readLine();
                lastResponseIP = clientSocket.getRemoteSocketAddress().toString();

                //System.out.print("ATTACHLOOP: got the following TCP resp... " + response);
                is.close();
                clientSocket.close();
            } catch (SocketException ex) {
                System.out.println("LOOP: Could not read TCP connection. Socket timeout?");
            } catch (IOException ex) {
                System.out.println("LOOP: Could not read TCP connection. IO Exception");
                ex.printStackTrace();
            }
            
            
            if (IS_ADMIN) {
                if (request != null){
                    if (request.equals(Settings.NETWORK_ATTACH_REQ)){
                        String data = null;
                        if (noOfNodes == 1)
                            //transmitBroadcast(Settings.NETWORK_ATTACH_CONFIRM);
                            data = Settings.NETWORK_ATTACH_CONFIRM;
                        else
                            //transmitBroadcast(Settings.NETWORK_ATTACH_MONITOR);
                            data = Settings.NETWORK_ATTACH_MONITOR;
                        
                        clientSocket = null;
                        try {
                            lastResponseIP = lastResponseIP.trim().replace("/", "");
                            lastResponseIP = lastResponseIP.substring(0, lastResponseIP.indexOf(":"));
                            System.out.println("LOOP - ADMIN: Trying to send NETWATT confirm. to ip: " +
                                    lastResponseIP);
                            clientSocket = new Socket(lastResponseIP, Settings.TCP_PORT);
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                            out.write(data);
                            out.close();
                            //clientSocket.close();
                        } catch (UnknownHostException ex) {
                            //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("LOOP - ADMIN: IP/Host Unknown");
                        } catch (IOException ex) {
                            //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("LOOP - ADMIN: I/O Exception... "
                                    + ex.getMessage());
                        }
                        
                        
                        
                        noOfNodes++;
                    }
                    
                }
                
                if ( response != null ){
                    if ( response.contains(Settings.DATAPREFIX) ){
                        response = response.replace(Settings.DATAPREFIX, "");
                        System.out.println("LOOP - ADMIN: just logged " + response + " from " + lastResponseIP);
                    }
                }
                
//                String received = null;
//                try {
//                    socket = servsocket.accept();
//                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    
//                    while (!in.ready()){}
//                    received = in.readLine();
//                    System.out.print(received);
//                    //in.close();
//                    //socket.close();
//                } catch (IOException ex) {
//                    System.exit(-2);
//                }
            }
            
            if (IS_WORKER) {
                if (request != null){

                }
                
                clientSocket = null;
                try {
                    System.out.println("LOOP - WORKER: Sending data to " + adminIP);
                    clientSocket = new Socket(adminIP, Settings.TCP_PORT);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    out.write(Long.toString(Math.round(Math.random() * 10) + 20));
                    out.close();
                    //clientSocket.close();
                } catch (UnknownHostException ex) {
                    //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("LOOP - WORKER: IP/Host Unknown");
                } catch (IOException ex) {
                    //Logger.getLogger(JavaApplication2.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("LOOP - WORKER: I/O Exception... "
                            + ex.getMessage());
                }
                
//                try {
//                    Socket clientsocket = new Socket(adminIP, Settings.TCP_PORT);
//                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
//                    out.write("DATA:" + Long.toString(20 + Math.round(Math.random() * 10)));
//                    out.close();
//                    clientsocket.close();
//                } catch (UnknownHostException ex) {
//                    System.exit(-3);
//                } catch (IOException ex) {
//                    System.exit(-4);
//                }
                
            }
                
            
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
               
                lastResponseIP = packet.getSocketAddress().toString();//socket.getRemoteSocketAddress().toString();
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
