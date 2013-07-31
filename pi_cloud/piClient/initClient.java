package pi_cloud.piClient;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.net.*;
import java.util.Enumeration;

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
            client = new Client(host, serverAddress, serverPort);
            client.interact();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    } 

} 
