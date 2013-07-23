package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.MalformedURLException;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client extends UnicastRemoteObject implements Client_Intf, Serializable {

    private Dispatcher_Intf dispatch;
    private StatusMonitor_Intf sm;
    private boolean busy;

    private String localHost;
    private String serverAddress;
    private short serverPort; 

    public Client(String h, String sa, short p) throws RemoteException {
        localHost = h;
        serverAddress = sa;
        serverPort = p;
        
        sm = new StatusMonitor();
        
        // Acquire server's dispatcher
        try {
            Registry reg = LocateRegistry.getRegistry( serverAddress, serverPort);
            dispatch = (Dispatcher_Intf) reg.lookup("Dispatcher");
            System.out.println("Success: Dispatcher at " + dispatch.getHost() + " found.\n");      
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error connecting to Dispatcher."); 
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("FAILURE: Client.java: Can't find Dispatcher in server registry.");
            e.printStackTrace();
         }/* catch (MalformedURLException e) {
            System.out.println("FAILURE: Client.java: URL reference to server registry is malformed."); 
            e.printStackTrace();
        } */
    }

    public void interact() {
        BufferedReader inputStream = new BufferedReader( new InputStreamReader(System.in) );
        int input = -1;
        boolean success= false;

            
        while (true) {
            System.out.println("_____");
            System.out.println("Available Actions: ");
            System.out.println("1: Register to cluster.");
            System.out.println("0: Exit.");

            try { input = Integer.parseInt( inputStream.readLine());
            } catch (Exception e) { System.out.println("ERROR: Unrecognised Input."); } 
            System.out.println("_____\n");
            
            switch (input) {
                case 1: try {
                            success = dispatch.register( (Client_Intf) this);
                        } catch (RemoteException e) {
                            System.out.println("FAILURE: Error connecting to Dispatcher.");
                            e.printStackTrace();
                        }
                        if (success) System.out.println("Registration successful.");
                        else System.out.println("FAILURE: Registration unsuccesul.");
                        break;
                case 0: System.out.println("Exiting...");
                        System.exit(1);
                default: System.out.println("ERROR: Entered option unavailable.");
                         continue;
            }
        }
    }

    public boolean executeAlgorithm() throws RemoteException {
        return true;
    }

    public StatusMonitor_Intf getStatusMonitor() throws RemoteException {
        return sm;
    }

    public boolean test() {
        return true;
    }

    /* Getters & Setters */
    public String getHost() {
        return localHost;
    } 


    // method managing dispatcher

}
