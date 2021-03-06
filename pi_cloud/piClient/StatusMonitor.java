package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;

/* Handles communciation with the Pi Manager regarding task or resource details.
    Though no implemented, some variables are available so as to allow for periodic update
     to the Pi Manager (refresh rates), rather than manual update as it currently is.
    Methods that acquire resource statistics should be implemented here
    , see executePs() for example.
 */
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
        executePs(); // update resource stats
        statMan.updateTaskDetails(client, taskID, taskType, taskStatus, input, output);
        statMan.updateResourceDetails(client, cpuUsage, memUsage, DRS, RSS, PMEM);
        printResourceDetails();
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

    /* Because a status monitor could be used for multiple different task executions, it is 
        important to refresh the default values of it before another task begins.
        Otherwise, the new task execution will contain some old and incorrect values.
     */
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
    
    /* Gets statistics on the amount of memory the process uses
        It executes the ps command to command line, and then parses it's output.
     */
    private boolean executePs() { // Gets stats on process memory
        String line = "failure";
        String res[] = new String[11]; 
        String[] cmd = {"/bin/sh", "-c", "ps aux | grep piClient"}; // need the first two strings (running from shell) so that the pipe can be used, doesn't work otherwise.

        try {
            Process pr = Runtime.getRuntime().exec(cmd);
            InputStream is = pr.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            System.out.println("Output:");
            line = br.readLine();
            System.out.println("Line: " + line);
            res = line.split("\\s+"); 
            for (int i = 0; i < 4; i++) {
                System.out.println(i + ": " + res[i]);
                switch (i) {
                    //case 7: DRS = Integer.parseInt(res[i]); //System.out.println("Initial DRS: " + DRS);
                            //continue;
                    case 2: cpuUsage = (short) Double.parseDouble(res[i]);
                            continue;
                    case 3: PMEM = (short) Double.parseDouble(res[i]);
                            continue;
                    default: continue;
                }
            } 

            System.out.println("DRS: " + DRS);
            System.out.println("%CPU: " + cpuUsage);
            System.out.println("%MEM:" + PMEM);
        } catch (Exception e) {
            System.out.println("FAILURE: StatusMonitor.java: Error executing \"ps\" and acquiring DRS, RSS and %MEM");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
