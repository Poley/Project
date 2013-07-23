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

    public boolean register(Client_Intf node) throws RemoteException {
        try {
            System.out.println("___\nDispatcher.java: Registration request recieved from " + node.getHost() + ".");
        } catch (Exception e) {
            System.out.println("Dispatcher.java: Error calling node.getHost() server-side.");
            e.printStackTrace();
        }
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
