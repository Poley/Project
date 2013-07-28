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

    protected void executeAlgorithm(String input) {
        if (input.contains("merge") ){
            for (int i = 0; i < cluster.size(); i++) {
                clister
            } 
        } 
    }

    // Define children within the network, used pre-execution of certain algorithms.
    private void defineClusterNetwork() {
        
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
