package pi_cloud.piClient;

import java.rmi.*;
import pi_cloud.piManager.*;

public interface StatusMonitor_Intf extends Remote {

    public short getCPUUsage() throws RemoteException; 
    public int getMemUsage() throws RemoteException ;
    public String getTaskType() throws RemoteException;
    public String getTaskStatus() throws RemoteException;
    public long getTaskID() throws RemoteException;
    public void setTaskID(long id) throws RemoteException;
    public boolean setTaskSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException;
    public boolean setResourceSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException;
    public String getHost() throws RemoteException;
    public void updateServer() throws RemoteException;
    public void setStatusManager(StatusManager_Intf sm) throws RemoteException;

}

