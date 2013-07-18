package pi_cloud;

import piClient.*;
public class Pi {

    //private Client_Intf node;
    private StatusMonitor_Intf statMon;
    private String task;
    private String taskStatus;
    private int ttc;
    private short cpuUsage;
    private int memUsage;

    protected Pi(StatusMonitor_Intf statM) {
        statMon = statM;
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


}
