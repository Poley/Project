package pi_cloud;

import piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements Client_Intf {

    Dispatcher_Intf dispatch;
    boolean busy;
    StatusMonitor_Intf sm;
    
    public Client() throws RemoteException {
        sm = new StatusMonitor();
    }

    public boolean executeAlgorithm() throws RemoteException {
        return true;
    }

    public StatusMonitor_Intf getStatusMonitor() throws RemoteException {
        return sm;
    }

}
