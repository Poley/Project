package pi_cloud.piClient;

import java.rmi.*;

public interface Client_Intf extends Remote {

    public int[] executeAlgorithm(String algo, int[] list) throws RemoteException;
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException;
    public String getHost() throws RemoteException;
    public MergeSorter_Intf getMS() throws RemoteException;

}
