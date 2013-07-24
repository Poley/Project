package pi_cloud.piManager;

import pi_cloud.piClient.*;

public class Pi {

    //private Client_Intf node;
    private StatusMonitor_Intf statMon;
    private String host;
    private String task = "Inactive";
    private String taskStatus = "Inactive";
    private int ttc = -1;
    private short cpuUsage = -1;
    private int memUsage = -1;

    protected Pi(StatusMonitor_Intf statM, String hostname) {
        statMon = statM;
        host = hostname;
    }

    protected boolean updateResourceDetails(short cpu, int mem) {
        cpuUsage = cpu;
        memUsage = mem;
        return true;
    }

    protected boolean updateTaskDetails(String t, String ts, int ttComplete) {
        task = t;
        taskStatus = ts;
        ttc = ttComplete;
        return true;
    }
    
    protected boolean updateStatusMonitor(StatusMonitor_Intf statM) {
        statMon = statM;
        return true;
    }

    protected StatusMonitor_Intf getStatusMonitor() {
        return statMon;
    }

    protected String getHost() {
        return host;
    }

    protected String getTask() {
        return task;
    } 


}
