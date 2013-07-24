package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;

public class StatusMonitor extends UnicastRemoteObject implements StatusMonitor_Intf, Serializable {

    private StatusManager_Intf statMan;
    private Client_Intf client;
    private String localHost;
    private String task;
    private String taskStatus;
    private short taskTTC;
    private byte algID; // ID identifying which algorithm is being executed
    
    private short taskRefreshRate; // 0 means no refresh, all updates are executed manually.

    private short cpuUsage;
    private int memUsage;
    private short resourceRefreshRate;

    public StatusMonitor(String lh, Client_Intf cli) throws RemoteException {
        localHost = lh;
        client = cli;
        task = "Inactive";
        taskStatus = "Inactive";
        taskTTC = 0;
    
        taskRefreshRate = 0;
        resourceRefreshRate = 0;

        cpuUsage = -1;
        memUsage = -1;
    }
    
    public String getTask() throws RemoteException { return task; }

    protected void setTask(String t) {
        task = t;
    } 

    public String getTaskStatus() throws RemoteException{ return taskStatus; }
    public short taskTTC() throws RemoteException { return taskTTC; }
    public byte getActiveAlgorithm() throws RemoteException { return algID; }
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
    public String getHost() throws RemoteException { return localHost; }
    
    public void updateServer() throws RemoteException { 
        statMan.updateTaskDetails(client, task, taskStatus, taskTTC);
    }

    public void setStatusManager(StatusManager_Intf sm) throws RemoteException {
        statMan = sm;
    }

    protected void printTaskDetails(){
        System.out.println("Task: " + task);
        System.out.println("Task Status: " + taskStatus);
        System.out.println("Task TTC: " + taskTTC);
        System.out.println("Task Refresh Rate: "+ taskRefreshRate);
    } 

    protected void printResourceDetails() {
        System.out.println("CPU Usage: " + cpuUsage);
        System.out.println("Memory Usage: " + memUsage);
        System.out.println("Resource Refresh Rate: "+ resourceRefreshRate);
    } 

}
