package pi_cloud.piClient;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
public class initClient {

    private static String host = "localhost";
    private static short port = 1098;

    public initClient() {}

    public static void main(String args[]) {
        Client client = null;
        Client_Intf registryStub = null;

        try { LocateRegistry.createRegistry(1098); }
        catch (Exception e) { e.printStackTrace(); }

        try {
            client = new Client(host, port);
            UnicastRemoteObject.unexportObject(client, true);
            registryStub = (Client_Intf) UnicastRemoteObject.exportObject(client, port);
            System.out.println("Client's remote object created successfully.");
        } catch (Exception e) { 
            System.out.println("initClient.java: Error creating exporting remote object.");
            e.printStackTrace(); 
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Security Manager successfully created.");
        } 

        try {
            Naming.rebind("//" + host + ":" + port + "/Client", registryStub);
            System.out.println("Client successfully bound to server registry.");
            System.out.println("Client has been successfully initialised.");
        } catch (Exception e) {
            System.out.println("initClient.java: Error in binding Client object to registry.");
            e.printStackTrace();
        }

    } 

} 
