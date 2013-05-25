/****************************************************************************************
 * Title      :    Communication Handler for Temperature Client
 * Developers :    Bogdan Ioan Sorlea - s121075@student.dtu.dk, 
 * 					      Robert Unnthorsson-s121049@student.dtu.dk
 * Purpose    :  	
 * Revision   : 	1.0    
 * Description:	
 ****************************************************************************************/
package tcp.client.comm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HandleClientTCP 
{
  // Variable declarations
  private Socket socket = null;
  private ObjectOutputStream os = null;
  private ObjectInputStream is  = null;
  
 
// Connect to the server
  public boolean connectToServer(String host,int port)
  {
	  System.out.print("Starting\n");
   // open a new socket to port and create streams
   try{
	   this.socket = new Socket(host,port);
	  // this.os = new ObjectOutputStream(this.socket.getOutputStream());
	 //  this.is = new ObjectInputStream(this.socket.getInputStream());
	   System.out.print("Connected to Server\n");
	   } 
   catch (Exception e){
	   System.out.print("Failed to Connect to Server\n" + e.toString());	
	   System.out.println(e.toString());
	   return false;}
   
	return true;
   }
  
// Method to send a generic object.
  public void send(Object o) 
  {
   try {
	   System.out.println("Sending " + o);
	   os.writeObject(o);
	   os.flush();
	   } 
   catch (Exception ex){ 
	   System.out.println(ex.toString());}
   }

// Method to receive a generic object.
  public Object receive() 
  {
   Object o = null;
   try{ 
	   o = is.readObject();
	  } 
   catch (Exception ex){
	   System.out.println(ex.toString());}
   return o;
  }
	
	
}
