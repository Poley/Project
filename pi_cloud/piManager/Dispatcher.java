package pi_cloud.piManager;

import java.util.ArrayList;
import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Dispatcher extends UnicastRemoteObject implements Dispatcher_Intf {

    Controller c;
    ArrayList<String> ipAddresses = new ArrayList<String>();
    short port = 1099; // generic port for now, update IP list to include pairs

    public Dispatcher(Controller contr) throws RemoteException {
        super(); 
        c = contr;
    }

    public boolean register(Client_Intf node) throws RemoteException {
        return c.addClient(node); 
    }

    public boolean unRegister(Client_Intf node) throws RemoteException {
        return c.removeClient(node);
    }

    // Let network know that the Pi_Manager is awake, asking them to register if active.
    protected boolean wakeUpNetwork() {
        return true;
    }

    protected void executeAlgorithm() {

    }

    // Define parents and children within the network, used pre-execution of certain algorithms.
    private void defineClusterNetworking() {

    }
    
    protected void addIPAndRegister(String ip) {
        try {
            Client_Intf cli = (Client_Intf) Naming.lookup("rmi://" + ip + ":" + port + "/Client"); 
            register(cli);
            ipAddresses.add(ip);
            System.out.println("Success: Client successfully found and added to cluster.");
        } catch (Exception e) {
            System.out.println("FAILURE: Dispatcher.java: Error looking up client. IP removed from list.");
            e.printStackTrace();
        } 
    } 

    /* ------- test methods -------- */
    protected boolean testRegistration() {
        // connect to 'remote' object of client
        return true;
    }

}
