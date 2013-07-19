package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class StatusMonitor extends UnicastRemoteObject implements StatusMonitor_Intf {

    private StatusManager_Intf statMan;
    private String task;
    private String taskStatus;
    private short taskTTC;
    private byte algID; // ID identifying which algorithm is being executed
    
    private short taskRefreshRate; // 0 means no refresh, all updates are executed manually.

    private short cpuUsage;
    private int memUsage;
    private short resourceRefreshRate;

    public StatusMonitor() throws RemoteException {
        taskRefreshRate = 0;
        resourceRefreshRate = 0;
    }
    
    public String getTask() throws RemoteException { return task; }
    public String getTaskStatus() throws RemoteException{ return taskStatus; }
    public short taskTTC() throws RemoteException { return taskTTC; }
    public byte getActiveAlgorithm() throws RemoteException { return algID; }
    // Needs to be a boolean?
    public boolean setTaskSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException{
        taskRefreshRate = refreshRate;
        return true;
    }

    public short getCPUUsage() throws RemoteException{ return cpuUsage; }
    public int getMemUsage() throws RemoteException { return memUsage; }
    public boolean setResourceSchedule(short refreshRate, StatusManager_Intf sm) throws RemoteException{
        resourceRefreshRate = refreshRate;
        return true;
    }

}
