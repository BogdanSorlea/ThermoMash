/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

/**
 *
 * @author Bogdan
 */
public class Settings {
    
    public static String BROADCAST_ADDRESS = "225.1.1.1";//10.16.0.0";//192.168.1.0";
    public static int BROADCAST_PORT = 4446;
    public static int TCP_PORT = 4445;
    //public static int ADMIN_PORT = 12200;
    
    public static String NETWORK_ATTACH_REQ = "NATTREQ";
    public static String NETWORK_ATTACH_CONF = "NATTCONF";
    public static String NETWORK_ATTACH_MONITOR = "NATTMONITOR";
    public static String NOTIFY_MONITOR = "NOTIFMONIT";
    public static String ADMIN_RUNNING = "ISADMINOK?";
    public static String ADMINOK = "ADMINOK";
    public static String MONITOR_RUNNING = "ISMONITOROK?";
    public static String MONITOROK = "MONITOROK";
    public static String DATAPREFIX = "DATA:";
    
    public static int BROADCAST_RECEIVE_TIMEOUT = 3000; // in miliseconds
    public static int BROADCAST_FAST_RECEIVE_TIMEOUT = 500; // in miliseconds
    
    public static int RESPONSE_DELAY_ITERATIONS = 100;
    public static String FIELD_DELIMITER = "|";
    public static int MONITOR_RESPONSE_ITERATIONS = 50;
    public static int ADMIN_RESPONSE_ITERATIONS = 50;
}
