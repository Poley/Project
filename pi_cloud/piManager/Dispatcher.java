package pi_cloud;

import piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Dispatcher extends UnicastRemoteObject implements Dispatcher_Intf {

    Controller c;

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

}
