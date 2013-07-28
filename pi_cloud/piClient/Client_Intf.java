package pi_cloud.piClient;

import java.rmi.*;

public interface Client_Intf extends Remote {

    public boolean executeAlgorithm(String algo) throws RemoteException;
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException;
    public String getHost() throws RemoteException;

}
