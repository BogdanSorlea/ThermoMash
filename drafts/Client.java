/****************************************************************************************
 * Title      :    Temperature Client Program
 * Developers :    Bogdan Ioan Sorlea - s121075@student.dtu.dk, 
 * 					      Robert Unnthorsson-s121049@student.dtu.dk
 * Purpose    :  	
 * Revision   : 	1.0    
 * Description:	
 ****************************************************************************************/
import tcp.client.comm.HandleClientTCP;	// Import the client communication handler


public class Client 
{
  public static void main(String[] args) 
  {
   // Variable declarations	  
   String host = "Robson_W700";  // Define a host server
   int port = 5050;             // Define a port
   HandleClientTCP client = new HandleClientTCP();

   // Connect to the server
   client.connectToServer(host, port);
   client.send("setTemp");   
}}
