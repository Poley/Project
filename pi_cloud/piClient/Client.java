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
    //private StatusMonitor_Intf sm;
    private StatusMonitor sm;
    private boolean busy;

    private String localHost;
    private String serverAddress;
    private short serverPort; 

    public Client(String h, String sa, short p) throws RemoteException {
        localHost = h;
        serverAddress = sa;
        serverPort = p;
        
        sm = new StatusMonitor(localHost, this);
        
        // Acquire server's dispatcher
        try {
            Registry reg = LocateRegistry.getRegistry( serverAddress, serverPort);
            dispatch = (Dispatcher_Intf) reg.lookup("Dispatcher");
            System.out.println("Success: Dispatcher at " + dispatch.getHost() + " found.");      
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error connecting to Dispatcher at " + serverAddress + ":" + serverPort); 
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("FAILURE: Client.java: Can't find Dispatcher in server registry.");
            e.printStackTrace();
         }
        
        // Registering client to server
        boolean success = false;
        try {
            success = dispatch.register( (Client_Intf) this, localHost);
        } catch (RemoteException e) {
            System.out.println("FAILURE: Error connecting to Dispatcher.");
            e.printStackTrace();
        }
        if (success) System.out.println("Success: Client is registered to server.");
        else System.out.println("FAILURE: Client.java: Registration unsuccessful.");

        System.out.println();
    }

    public void interact() {
        BufferedReader inputStream = new BufferedReader( new InputStreamReader(System.in) );
        int input = -1;
        boolean success= false;

            
        while (true) {
            System.out.println("_____");
            System.out.println("Available Actions: ");
            System.out.println("2: Print client details.");
            //System.out.println("1: Register to cluster.");
            System.out.println("0: Exit.");

            try { input = Integer.parseInt( inputStream.readLine());
            } catch (Exception e) { System.out.println("ERROR: Unrecognised Input."); } 
            System.out.println("_____\n");
            
            switch (input) {
                case 3: sm.setTask("Testing testing");
                case 2: printDetails();
                        break;
                case 1: try {
                            success = dispatch.register( (Client_Intf) this, localHost);
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

    public void printDetails() {
        System.out.println("Host: " + localHost);
        System.out.println();
        sm.printTaskDetails();
        System.out.println();
        sm.printResourceDetails();
    } 


    // method managing dispatcher

}
