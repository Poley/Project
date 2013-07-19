package pi_cloud.piClient;

import java.rmi.*;

public interface Client_Intf extends Remote {

    public boolean executeAlgorithm() throws RemoteException;
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException;

}
