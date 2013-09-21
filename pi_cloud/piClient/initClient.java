package pi_cloud.piClient;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.net.*;
import java.util.Enumeration;

/* Run this class to create a client locally.
   The configuration for connecting to the server is all in here, change the server address and 
        port to the one which you required to connect to.
 */
public class initClient {

    private static String host;
    private static String serverAddress = "192.168.100.100";
    private static short serverPort = 1099;

    public initClient() {}

    public static void main(String args[]) {
        Client client = null;
        Client_Intf registryStub = null;
        
        // Finding address of local eth0 address
        try {
            NetworkInterface ni = NetworkInterface.getByName("eth0");
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

            while (inetAddresses.hasMoreElements() ) {
                InetAddress ia = inetAddresses.nextElement();
                if(!ia.isLinkLocalAddress() ) {
                    host = ia.getHostAddress();
                    System.out.println("Local IP Address: " + host); 
                } 
            } 
        } catch (Exception e) { } 
        
        try {
            // NOTE: Currently the the current time is being used instead of the IP address, this is required when testing locally on one machine.
                // this is caused by the fact hashmaps are used, and if multiple process have the same IP, then the same <key,value> pair will be used between them all.
                //  Using current system time gives each client a unique ID.
            client = new Client( "" + System.currentTimeMillis(), serverAddress, serverPort);
         //   client.interact();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    } 

} 
