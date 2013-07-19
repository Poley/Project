package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class StatusManager extends UnicastRemoteObject implements StatusManager_Intf {

    Cluster cluster;
    
    public StatusManager (Cluster c) throws RemoteException {
        super();
        cluster = c;
    }

    public boolean updateResourceDetails(Client_Intf node, short cpuUsage, int memUsage) throws RemoteException {
        if (!cluster.updateResourceDetails(node, cpuUsage, memUsage)) return false;
        else return true;
    }

    public boolean updateTaskDetails(Client_Intf node, String task, String taskStatus, int ttc) throws RemoteException {
        if (!cluster.updateTaskDetails(node, task, taskStatus, ttc) ) return false;
        else return true;
    }

    // boolean?
    protected boolean setResourceSchedule(short refreshRate) {
        StatusMonitor_Intf[] sm = cluster.getStatusMonitors();
        for (StatusMonitor_Intf s : sm) {
            try { s.setResourceSchedule(refreshRate, this); }
            catch (RemoteException e) { e.printStackTrace(); }
        }
        return true;
    }

    // boolean?
    protected boolean setTaskSchedule(short refreshRate) {
        StatusMonitor_Intf[] sm = cluster.getStatusMonitors();
        for (StatusMonitor_Intf s : sm) {
            try { s.setTaskSchedule(refreshRate, this); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return true;
    }
}
