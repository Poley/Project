package pi_cloud.piClient;

import java.rmi.*;

public interface MergeSorter_Intf extends Remote {

    public int[] sort(int[] list) throws RemoteException;

    public void setListThreshold(short t) throws RemoteException;
    public void setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException;
    public boolean hasChildren() throws RemoteException; 
    public String[] getChildHostnames() throws RemoteException;
    public String getHostname() throws RemoteException;
}
