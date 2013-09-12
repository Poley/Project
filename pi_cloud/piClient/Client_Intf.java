package pi_cloud.piClient;

import java.rmi.*;

public interface Client_Intf extends Remote {
    
    public int[] executeMergeSort(long taskID, int[] input) throws RemoteException;
    
    public StatusMonitor_Intf getStatusMonitor() throws RemoteException;
    public String getHostname() throws RemoteException;
    public MergeSorter_Intf getMS() throws RemoteException;
    public String[] getMSChildHostnames() throws RemoteException;
}
