/****************************************************************************************
 * Title      :    Temperature Server
 * Developers :    Bogdan Ioan Sorlea - s121075@student.dtu.dk, 
 * 					Robert Unnthorsson-s121049@student.dtu.dk
 * Purpose    :  	
 * Revision   : 	1.0    
 * Description:	
 ****************************************************************************************/
import tcp.server.comm.HandleServerTCP;		// The communication handler

public class Server 
{
    public static void main(String args[]) 
    {
    	/* Create the Handle Connection object*/
    	HandleServerTCP connection = 
    			new HandleServerTCP();  
    	connection.openSocket(5050);			// Open socket 5050 for communications.
    	
    	while (true) 							// infinite loop - loops once for each client
        {
         connection.scanForClient();				// Wait for a connection from a client
        
         if (connection == null)				// If it failed send and error message
         {
        	// Do something 
         }
         else									// Connection ok.
         {
           connection.clientInfo();				// Retrieve info from current client
           connection.init();					// Retrieve command from client
         }

        }
    }
}
