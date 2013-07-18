package pi_cloud;

import java.rmi.*;

public interface Client_Intf extends Remote {

    public boolean executeAlgorithm() throws RemoteException;
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException;

}
