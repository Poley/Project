package pi_cloud.piClient;

import java.rmi.*;

public interface MergeSorter_Intf extends Remote {

    public void sort(Client cli, MergeSorter_Intf parentNode, long taskID, int[] list, boolean isRoot) throws RemoteException;
    public void sortingCallback (int[] result) throws RemoteException;

    public void setListThreshold(short t) throws RemoteException;
    public void setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException;
    public boolean hasChildren() throws RemoteException; 
    public String[] getChildHostnames() throws RemoteException;
    public String getHostname() throws RemoteException;
}
