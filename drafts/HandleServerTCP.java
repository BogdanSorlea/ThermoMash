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

 
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// The TCP communication handler Class for the server
public class HandleServerTCP
{
 // Variable Declarations
 public String inputRequest;			// Request from client
 private ServerSocket serverSocket;		// Server socket object
 private Socket clientSocket;			// Client socket object
 private ObjectInputStream ois;			// Input stream
 private ObjectOutputStream oos;		// Output stream
 
 // The constructor for the connecton handler
 public HandleServerTCP()
 { }

 // Function to open the communication socket
 public boolean openSocket(int port)
 {
	serverSocket = null;
	try 
	{
	 serverSocket = new ServerSocket(port);
	 System.out.println("Server listening on port " + port);
	} 
	catch (IOException e)
	{
	 System.out.println("Error: Cannot listen on port " + port + ": " + e);
	 System.exit(1);
	 return false;
	}
	return true;
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
    while (this.readCommand()) {}
   } 
   catch (IOException e) 
   {
    e.printStackTrace();
   }
 }

 // Receive and process incoming command from client socket 

 public boolean readCommand() 
 {
     String s = null;

     try{ s = (String)ois.readObject();}
     catch (Exception e){s = null;}
     
     if (s == null) 
     {
         this.closeSocket();
         return false;
     }

     // invoke the appropriate function based on the command 
     if (s.equals("setTemp")) 
     { 
       System.out.println("the request setTemp was received from the client");
       this.getDate(); 
     }       
     else 
     { 
       this.sendError("Invalid command: " + s); 
     }
     return true;
 }

 private void getDate()	// uses the date service to get the date
 {        
    // String currentDateTime = theDateService.getDateAndTime();
    // this.send(currentDateTime);
 }


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
