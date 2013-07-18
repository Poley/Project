package pi_cloud;

import java.rmi.*;

public interface StatusMonitor_Intf extends Remote {

    public short getCPUUsage() throws RemoteException; 
    public int getMemUsage() throws RemoteException ;
    public String getTaskStatus() throws RemoteException;
    public boolean setTaskSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException;
    public boolean setResourceSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException;

}

