/****************************************************************************************
 * Title      :    Handler for Temperature Server
 * Developers :    Bogdan Ioan Sorlea - s121075@student.dtu.dk, 
 * 					      Robert Unnthorsson-s121049@student.dtu.dk
 * Purpose    :  	
 * Revision   : 	1.0    
 * Description:	
 ****************************************************************************************/
// Package declaration
 package tcp.server.comm;

 
import java.net.*;
import java.io.*;

// The TCP communication handler Class for the server
public class HandleServerTCP
{
 // Variable Declarations
 public String inputRequest;			// Request from client
 private String IPAddress;                      // IP Address of the socket holder.
 private ServerSocket serverSocket;		// Server socket object
 private Socket clientSocket;			// Client socket object
 private ObjectInputStream ois;			// Input stream
 private ObjectOutputStream oos;		// Output stream
 
 // The constructor for the connecton handler
 public HandleServerTCP()
 { }

 // Function to open the communication socket
 public boolean openSocket(String IP, int port)
 {
	serverSocket = null;
        try 
	{ 
  	 serverSocket = new ServerSocket();
         serverSocket.bind(new InetSocketAddress(IP, port));
         System.out.println("Opened socket on port: " +
         serverSocket.getLocalPort() + "...");     
        }
        catch(IOException e)
         {
         e.printStackTrace();  
         }
  return true;}

public String getIPAddress()
{
    try {
        InetAddress addr = InetAddress.getLocalHost();            
        IPAddress = addr.getHostAddress().trim();
        } 
    catch (UnknownHostException e) {};
    return IPAddress;
}

 // Function to scan for a client to communicate with. 
 public boolean scanForClient()
 {
   Socket clientSocket = null;
   try 
   {
    this.clientSocket = serverSocket.accept(); //waits here (forever) until a client connects
    System.out.println("Server has just accepted socket connection from a client");
   } 
   catch (IOException e) 
   {
     System.out.println("Accept failed: 5050 " + e); // Prints out an error message
     System.exit(1);
     return false;
   }		 
   return true;
 }
 
 // Function to get information on the connected Client.
 public void clientInfo()
 { 
  //Client address
  InetAddress remoteIp = clientSocket.getInetAddress();
  System.out.print("The client's IP is: " + remoteIp + "\n");
 }
 
 // The main execution method 
 public void init() 
 {
   String inputLine;
   try 
   {
    this.ois = new ObjectInputStream(clientSocket.getInputStream());
    this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
    //while (this.readCommand() != null) {}
   } 
   catch (IOException e) 
   {
    e.printStackTrace();
   }
 }

 // Receive and process incoming command from client socket 
/*
 public String readCommand() 
 {
     String s = null;

        try {
            str = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
      return s;
 }

 private void getDate()	// uses the date service to get the date
 {        
    // String currentDateTime = theDateService.getDateAndTime();
    // this.send(currentDateTime);
 }
*/

 // Send a message back through to the client socket as an Object
 private void send(Object o) 
 {
     try 
     {
         System.out.println("Sending " + o);
         this.oos.writeObject(o);
         this.oos.flush();
     } 
     catch (Exception ex) 
     {
         ex.printStackTrace();
     }
 }
 
 // Send a pre-formatted error message to the client 
 public void sendError(String msg) 
 {
     this.send("error:" + msg);	
 }
 
 // Close the client socket 
 public void closeSocket()		//close the socket connection
 {
     try 
     {
         this.oos.close();
         this.ois.close();
         this.clientSocket.close();
     } 
     catch (Exception ex) 
     {
         System.err.println(ex.toString());
     }
 }

}
