package pi_cloud.piManager;

import pi_cloud.piClient.*;

public class Pi {

    //private Client_Intf node;
    private StatusMonitor_Intf statMon;
    private String host;

    private long taskId = 12345;
    private String taskType = "Inactive";
    private String taskStatus = "Inactive";
    private int ttc = 0;

    private short cpuUsage = 0;
    private int memUsage = 0;
    private int DRS = 0;
    private int RSS = 0;
    private short PMEM = 0; // process memory


    protected Pi(StatusMonitor_Intf statM, String hostname) {
        statMon = statM;
        host = hostname;
    }

    protected boolean updateResourceDetails(short cpu, int mem, int dr, int rs, short pm) {
        cpuUsage = cpu;
        memUsage = mem;
        DRS = dr;
        RSS = rs;
        PMEM = pm;
        return true;
    }

    protected boolean updateTaskDetails(long tId, String tt, String ts, int ttComplete) {
        taskId = tId;
        taskType = tt;
        taskStatus = ts;
        ttc = ttComplete;
        return true;
    }
    
    protected boolean updateStatusMonitor(StatusMonitor_Intf statM) {
        statMon = statM;
        return true;
    }

    protected StatusMonitor_Intf getStatusMonitor() { return statMon; }

    protected String getHost() { return host; }

    protected long getTaskId() { return taskId; }
    protected String getTaskType() { return taskType; } 
    protected String getTaskStatus() { return taskStatus; }
    protected short getPMem() { return PMEM; };

    protected void printResourceStats() {
        System.out.println("\tDRS: " + DRS + " kb");
        System.out.println("\tRSS: " + RSS + " kb");
        System.out.println("\t%MEM: " + PMEM + "%");
    } 


}
