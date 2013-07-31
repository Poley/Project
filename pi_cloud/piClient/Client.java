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

    private MergeSorter_Intf mergeS;

    public Client(String h, String sa, short p) throws RemoteException {
        localHost = h;
        serverAddress = sa;
        serverPort = p;
        mergeS = new MergeSorter(null, null);
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
            System.out.println("1: Update Resource Statistics.");
            System.out.println("2: Print client details.");
            System.out.println("0: Exit.");

            try { input = Integer.parseInt( inputStream.readLine());
            } catch (Exception e) { System.out.println("ERROR: Unrecognised Input."); } 
            System.out.println("_____\n");
            
            int[] list = {5,3,2,1,4,6,10,105,3,6,23,1,0,12};

            switch (input) {
                case 2: printDetails();
                        break;
                case 1: if (sm.updateResourceStats()) System.out.println("Resource stats updated."); 
                        break;
                case 0: try { 
                            if ( dispatch.unRegister (this) ) System.out.println("Client unregistered from server. Now exiting...");
                            else System.out.println("Error unregistering from server. Continuing to exit...");
                        } catch (RemoteException e){};
                        System.exit(1);
                default: System.out.println("ERROR: Entered option unavailable.");
                         continue;
            }
        }
    }

    public int[] executeMergeSort(int[] input) throws RemoteException {
        return mergeS.sort( input);
    }

    public void printDetails() {
        System.out.println("Host: " + localHost);
        System.out.println();
        sm.printTaskDetails();
        System.out.println();
        sm.printResourceDetails();
    } 

    /* Getters & Setters */
    public String getHost() { return localHost; } 
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException { return sm; }
    public MergeSorter_Intf getMS() throws RemoteException { return (MergeSorter_Intf) mergeS; } 

}
