package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;


public class StatusMonitor extends UnicastRemoteObject implements StatusMonitor_Intf, Serializable {

    private StatusManager_Intf statMan;
    private Client_Intf client;
    private String localHost;
    private long taskID = 12345;
    private String taskType = "inactive";
    private String taskStatus = "inactive";
    private String input = "empty";
    private String output = "empty";
    private short taskTTC = 0;
    private byte algID; // ID identifying which algorithm is being executed
    
    private short taskRefreshRate = 0; // 0 means no refresh, all updates are executed manually.

    private short cpuUsage = 0;
    private int memUsage = 0;
    private int DRS = 0; // Data Resident Size.
    private int RSS= 0; // Resident Set Size.
    private short PMEM = 0; // % RAM used.
    private short resourceRefreshRate = 0;

    public StatusMonitor(String lh, Client_Intf cli) throws RemoteException {
        localHost = lh;
        client = cli;
    }

    public void setStatusManager(StatusManager_Intf sm) throws RemoteException {
        statMan = sm;
    }

    public long getTaskID() throws RemoteException { return taskID; }
    public void setTaskID(long id) throws RemoteException { taskID = id; }
    
    public String getTaskType() throws RemoteException { return taskType; }
    protected void setTaskType(String t) { taskType = t; } 

    public String getTaskStatus() throws RemoteException{ return taskStatus; }
    public void setTaskStatus(String st) throws RemoteException { taskStatus = st; }

    public String getInput() throws RemoteException { return input; }
    public void setInput(String in) throws RemoteException { input = in; }
    public String getOutput() throws RemoteException { return output; }
    public void setOutput(String out) throws RemoteException { output = out; }

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
    
    /* Called by server to request update on Status Monitor statistics. */
    public void updateServer() throws RemoteException { 
        // executePs(); // update resource stats
        statMan.updateTaskDetails(client, taskID, taskType, taskStatus, input, output);
        statMan.updateResourceDetails(client, cpuUsage, memUsage, DRS, RSS, PMEM);
    }

    /* Printing methods */
    protected void printTaskDetails(){
        System.out.println("Task Type: " + taskType);
        System.out.println("Task Status: " + taskStatus);
        System.out.println("Task TTC: " + taskTTC);
        System.out.println("Task Refresh Rate: "+ taskRefreshRate);
    } 

    protected void printResourceDetails() {
        System.out.println("CPU Usage: " + cpuUsage);
        System.out.println("Memory Usage: " + memUsage);
        System.out.println("Resource Refresh Rate: "+ resourceRefreshRate);
    } 

    /* Acquiring resource statistics */
    protected boolean updateResourceStats() {
        return executePs();
    } 

    protected void refreshDefaultValues() {
        taskID = 12345;
        taskType = "inactive";
        taskStatus = "inactive";
        input = "empty";
        output = "empty";
        taskTTC = 0;
        
        cpuUsage = 0;
        memUsage = 0;
        DRS = 0; // Data Resident Size.
        RSS= 0; // Resident Set Size.
        PMEM = 0; // % RAM used.
        resourceRefreshRate = 0;
    } 

    private boolean executePs() { // Gets stats on process memory
        String line = "failure";
        String res[] = new String[11]; 
        String[] cmd = {"/bin/sh", "-c", "ps v | grep java"}; // need the first two strings (running from shell) so that the pipe can be used, doesn't work otherwise.

        try {
            Process pr = Runtime.getRuntime().exec(cmd);
            InputStream is = pr.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            System.out.println("Output:");
            line = br.readLine();
            res = line.split("\\s+"); 
            for (int i = 0; i < res.length; i++) {
                //System.out.println(i + ": " + res[i]);
                switch (i) {
                    case 7: DRS = Integer.parseInt(res[i]);
                            continue;
                    case 8: RSS = Integer.parseInt(res[i]);
                            continue;
                    case 9: PMEM = Short.parseShort(res[i]);
                            continue;
                    default: continue;
                }
            } 

            System.out.println("DRS: " + DRS);
            System.out.println("RSS: " + RSS);
            System.out.println("%MEM:" + PMEM);
        } catch (Exception e) {
            System.out.println("FAILURE: StatusMonitor.java: Error executing \"ps\" and acquiring DRS, RSS and %MEM");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
