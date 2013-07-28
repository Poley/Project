package pi_cloud.piClient;

import java.rmi.*;

public interface MergeSorter_Intf extends Remote {

    public boolean updateChildren(MergeSorter_Intf childA, MergeSorter_Intf childB) throws RemoteException;
    public int[] sort(int[] list) throws RemoteException;

    public boolean setListThreshold(short t) throws RemoteException;
}
