package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.MalformedURLException;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/* Pi Manager dispatcher communcicates with this client class, which handles executiong of algorithms.
 */
public class Client extends UnicastRemoteObject implements Client_Intf, Serializable {

    private Dispatcher_Intf dispatch;
    private StatusMonitor sm;

    private String localHost;
    private String serverAddress;
    private short serverPort; 

    private MergeSorter_Intf mergeS;

    public Client(String h, String sa, short p) throws RemoteException {
        localHost = h;
        serverAddress = sa;
        serverPort = p;
        sm = new StatusMonitor(localHost, this);
        mergeS = new MergeSorter(null, null, h, sm);
        
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

    public void executeMergeSort(long taskID, int[] input) throws RemoteException {
        mergeS.sort(this, null, taskID, input, true);
    }

    // Called when a algorithm has finished execution.
    public void taskFinished(int[] result) {
        try {
            dispatch.mergeSortResult(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        } 
    } 

    // Returns the hostname of all the children the MergeSorter class has.
    public String[] getMSChildHostnames() { 
        String[] children = new String[0];
        try {
            children = mergeS.getChildHostnames();
            return children;
        } catch (RemoteException e) {
            e.printStackTrace();
        } 
        return children;
     }

    /* Getters & Setters */
    public String getHostname() { return localHost; } 
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException { return sm; }
    public MergeSorter_Intf getMS() throws RemoteException { return (MergeSorter_Intf) mergeS; } 

}
