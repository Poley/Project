package pi_cloud.piManager;
import pi_cloud.piClient.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Dispatcher extends UnicastRemoteObject implements Dispatcher_Intf {

    Controller c;
    ArrayList<String> ipAddresses = new ArrayList<String>();

    MergeSorter_Intf master = null;

    public Dispatcher(Controller contr) throws RemoteException {
        super(); 
        c = contr;
    }

    public boolean register(Client_Intf node, String hostname) throws RemoteException {
        try {
            System.out.println("___\nDispatcher.java: Registration request recieved from " + node.getHostname() + ".");
        } catch (Exception e) {
            System.out.println("Dispatcher.java: Error calling node.getHost() server-side.");
            e.printStackTrace();
        }
        return c.addClient(node, hostname); 
    }

    public boolean unRegister(Client_Intf node) throws RemoteException {
        return c.removeClient(node);
    }

    // Returns time-to-execute
    protected int[] executeMergeSort(long taskID, Client_Intf[] clients, int[] input) {
        int[] sorted = new int[input.length];

        try {
            if (clients.length > 0) {
                long startTime = System.nanoTime();
                sorted = clients[0].getMS().sort(taskID, input);
                long endTime = System.nanoTime();
                long tte = endTime - startTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return sorted;
    }

    // Define children within the network, used pre-execution of certain algorithms.
    protected void defineClusterNetwork(Client_Intf[] clients, int listLength) {
        System.out.println("Clients available = " + clients.length);

        for (int i = 0; i < clients.length; i++) {
           try {
               if ( ((clients.length) - i) > 2) { // two children avilable to be assigned
                    clients[i].getMS().setChildren( clients[i+1].getMS(), clients[i+2].getMS() );
                    i=i+2;
                } else if ( ((clients.length)-i) == 2 ) { // only one child available to be assigned
                    clients[i].getMS().setChildren( clients[i+1].getMS(), null);
                    i++;
                } 
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("FAILURE: Dispatcher.java: Error connecting to client MergeSorter.");
            } 
        } 
    }

    protected HashMap<String, String[]> getClusterNetwork(Client_Intf[] clients) {
        HashMap<String, String[]> clusterNetwork = new HashMap<String, String[]>();
        
        try {    
            for (int i = 0; i < clients.length; i++) {
                String[] childHostnames = clients[i].getMSChildHostnames();
                clusterNetwork.put(clients[i].getHostname(), childHostnames);
            } 
        } catch (RemoteException e) {
            e.printStackTrace();
        } 
        return clusterNetwork;
    } 

    /* Getters & Setters */
    public String getHost() throws RemoteException { return c.getHost(); } 

}
