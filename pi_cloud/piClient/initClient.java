package pi_cloud.piClient;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.net.*;
import java.util.Enumeration;

public class initClient {

    private static String host;
    private static short port = 1097;

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


        //host = "localhost";

        // Setting up RMI Objects
        try { LocateRegistry.createRegistry( port); }
        catch (Exception e) { e.printStackTrace(); }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Success: Security Manager created.");
        } 

        try {
            client = new Client(host, port);
            
            try {UnicastRemoteObject.unexportObject(client, true); }
            catch (Exception e) {};
            registryStub = (Client_Intf) UnicastRemoteObject.exportObject(client, port);
            System.out.println("Success: Client exported to registry.");
           
            String rmiRef = "//" + host + ":" + port + "/Client";

            try { Naming.unbind( rmiRef);
            } catch (NotBoundException e) {}
            Naming.rebind( rmiRef, registryStub);
            
            System.out.println("Success: Client bound to reference at: \"" + rmiRef + "\"");
            System.out.println("Success: Client initialised.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error exporting Client to registry.");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("FAILURE: Client.java: URL binding the Client object is malformed.");
            e.printStackTrace();
        }

        client.interact();

    } 

} 
