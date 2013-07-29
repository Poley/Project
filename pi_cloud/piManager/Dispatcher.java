package pi_cloud.piManager;

import java.util.ArrayList;
import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Dispatcher extends UnicastRemoteObject implements Dispatcher_Intf {

    Controller c;
    ArrayList<String> ipAddresses = new ArrayList<String>();

    public Dispatcher(Controller contr) throws RemoteException {
        super(); 
        c = contr;
    }

    public boolean register(Client_Intf node, String hostname) throws RemoteException {
        try {
            System.out.println("___\nDispatcher.java: Registration request recieved from " + node.getHost() + ".");
        } catch (Exception e) {
            System.out.println("Dispatcher.java: Error calling node.getHost() server-side.");
            e.printStackTrace();
        }
        return c.addClient(node, hostname); 
    }

    public boolean unRegister(Client_Intf node) throws RemoteException {
        return c.removeClient(node);
    }

    protected void executeAlgorithm(String alg, Client_Intf[] clients) {
        
        int[] input = {6,5,4,3,4,5,6,7,8,7,8,7,8,88,9,8,6,32,1,2,3,44,5,67,76,45,7,9,3,8,26,15,1,783733,2,61,562,37,48,9,0,49,4};
        try { 
            int[] sorted = clients[0].executeAlgorithm("merge", input); 
            System.out.println("\nInput:") ;
            for (int i : input) System.out.print(" " + i);
            System.out.println("\nSorted:");
            for (int j : sorted) System.out.print(" " + j);
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    // Define children within the network, used pre-execution of certain algorithms.
    protected void defineClusterNetwork(Client_Intf[] clients) {
        try {
            for (int i = 0; i < clients.length; i=i+3) {
                MergeSorter_Intf msi = clients[i].getMS();
                msi.setChildren( clients[i+1].getMS(), clients[i].getMS() );
            } 
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public int[] getMergeSortInput() throws RemoteException {
        int[] input = {6,5,4,3,2,1};
        return input;
    } 
   
    /* Getters & Setters */
    public String getHost() throws RemoteException {
        return c.getHost();
    } 

    /* ------- test methods -------- */
    protected boolean testRegistration() {
        // connect to 'remote' object of client
        return true;
    }

}
