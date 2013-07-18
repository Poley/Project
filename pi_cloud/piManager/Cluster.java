package pi_cloud;

import piClient.*;
import java.util.HashMap;

public class Cluster {
    
    //private ArrayList<Pi> piCluster;
    private HashMap<Client_Intf, Pi> piCluster;

    protected Cluster() {
        piCluster = new HashMap<Client_Intf, Pi>();
    }

    protected boolean addClient(Client_Intf n) {
        try { 
            piCluster.put(n, new Pi( n.getStatusMonitor()) );
            return true;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    } 
    
    protected boolean removeClient(Client_Intf n) {
        if (piCluster.containsKey(n) ) {
            piCluster.remove(n); 
            return true;
        } else { 
            System.out.println("Client is already unregistered.");
            return false;
        }
    }

    protected boolean updateResourceDetails(Client_Intf n, short cpuUsage, int memUsage) {
        Pi node = piCluster.get(n); // might be worth validating it exists
        node.updateResourceDetails(cpuUsage, memUsage);
        piCluster.put(n, node);
        return true;
    }

    protected boolean updateTaskDetails(Client_Intf n, String task, String taskStatus, int ttc) {
        Pi node = piCluster.get(n);
        node.updateTaskDetails(task, taskStatus, ttc);
        piCluster.put(n, node);
        return true;
    }
    
    protected HashMap<Client_Intf, Pi> getFullDetails() {
        return piCluster;
    }

    protected StatusMonitor_Intf[] getStatusMonitors() {
        StatusMonitor_Intf statMons[] = new StatusMonitor_Intf[ piCluster.size()];
        Client_Intf clients[] = (Client_Intf[]) piCluster.keySet().toArray();
   
        // create array of available stat monitors
        for (int i = 0; i < statMons.length; i++) {
            statMons[i] = piCluster.get(clients[i]).getStatusMonitor();

        }
        return statMons;
    }

    protected int getClusterSize() {
        return piCluster.size();
    }



    /* ------- test methods -------- */
    public boolean testAlgAndStat() {
        

        return true;
    }

    public boolean testTaskAndRes() {
        return true;
    }

}
