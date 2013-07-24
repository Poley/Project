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

    public boolean updateTaskDetails(Client_Intf node, String task, String taskStatus, short ttc) throws RemoteException {
        if (!cluster.updateTaskDetails(node, task, taskStatus, ttc) ) return false;
        else return true;
    }

    protected boolean setResourceSchedule(short refreshRate) {
        StatusMonitor_Intf[] sm = cluster.getStatusMonitors();
        for (StatusMonitor_Intf s : sm) {
            try { s.setResourceSchedule(refreshRate, this); }
            catch (RemoteException e) { e.printStackTrace(); }
        }
        return true;
    }

    protected boolean setTaskSchedule(short refreshRate) {
        StatusMonitor_Intf[] sm = cluster.getStatusMonitors();
        for (StatusMonitor_Intf s : sm) {
            try { s.setTaskSchedule(refreshRate, this); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return true;
    }
    
    protected void requestTaskDetailUpdate(StatusMonitor_Intf[] statMons) {
        for (int i = 0; i < statMons.length; i++) {
            try {
                statMons[i].updateServer();
            } catch (RemoteException e) {
                System.out.println("FAILURE: StatusManager.java: Error contacting client.");
                e.printStackTrace();
            } 
        } 
        //System.out.println("All tasks retrieved.");
    } 

    protected void setClientsStatusManager(StatusMonitor_Intf sm) {
        try {
            sm.setStatusManager(this);
        } catch (RemoteException e) {
            System.out.println("FAILURE: StatusManager.java: Error contact status monitor.");
            e.printStackTrace();
        } 
    }

}
