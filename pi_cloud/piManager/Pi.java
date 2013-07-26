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
    private int DRS = -1;
    private int RSS = -1;
    private double PMEM = -1;

    protected Pi(StatusMonitor_Intf statM, String hostname) {
        statMon = statM;
        host = hostname;
    }

    protected boolean updateResourceDetails(short cpu, int mem, int dr, int rs, double pm) {
        cpuUsage = cpu;
        memUsage = mem;
        DRS = dr;
        RSS = rs;
        PMEM = pm;
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

    protected void printResourceStats() {
        System.out.println("\tDRS: " + DRS + " kb");
        System.out.println("\tRSS: " + RSS + " kb");
        System.out.println("\t%MEM: " + PMEM + "%");
    } 


}
