package pi_cloud.piClient;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;

public class initClient {

    private static String host = "localhost";
    private static short clientPort = 1099;

    public initClient() {}

    public static void main(String args[]) {
        Client client = null;
        Client_Intf registryStub = null;

        try { LocateRegistry.createRegistry(1099); }
        catch (Exception e) { e.printStackTrace(); }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Success: Security Manager created.");
        } 

        try {
            client = new Client(host, clientPort);
            
            try {UnicastRemoteObject.unexportObject(client, true); }
            catch (Exception e) {};
            registryStub = (Client_Intf) UnicastRemoteObject.exportObject(client, clientPort);
            System.out.println("Success: Client exported to registry.");
            
            try { Naming.unbind("//" + host + ":" + clientPort + "/Client");
            } catch (NotBoundException e) {}
            Naming.rebind("//" + host + ":" + clientPort + "/Client", registryStub);
            
            System.out.println("Success: Client bound to reference.");
            System.out.println("Success: Client initialised.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error exporting Client to registry.");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("FAILURE: Client.java: URL binding the Client object is malformed.");
            e.printStackTrace();
        } 

    } 

} 
