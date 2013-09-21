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

    public boolean updateResourceDetails(Client_Intf node, short cpuUsage, int memUsage, int DRS, int RSS, short PMEM) throws RemoteException {
        if (!cluster.updateResourceDetails(node, cpuUsage, memUsage, DRS, RSS, PMEM)) { 
            System.out.println("StatusManager.java: Error updating resources");
            return false;
        }
        else return true;
    }

    public boolean updateTaskDetails(Client_Intf node, long taskId, String taskType, String taskStatus, String input, String output) throws RemoteException {
        if (!cluster.updateTaskDetails(node, taskId, taskType, taskStatus, input, output) ) return false;
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
    
    protected void requestUpdate(StatusMonitor_Intf[] statMons) {
        for (int i = 0; i < statMons.length; i++) {
            try {
                statMons[i].updateServer();
            } catch (RemoteException e) {
                System.out.println("FAILURE: StatusManager.java: Error contacting client.");
                e.printStackTrace();
            } 
        } 
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
