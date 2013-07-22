package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Cluster {
    
    //private ArrayList<Pi> piCluster;
    private HashMap<Client_Intf, Pi> piCluster;
    private StatusManager statMan;

    protected Cluster(String host, short port) {
        piCluster = new HashMap<Client_Intf, Pi>();
        System.out.println("Success: Cluster initialised.");
        
        // creating status manager
        try {
            statMan = new StatusManager(this);
            UnicastRemoteObject.unexportObject(statMan, true);
            StatusManager_Intf registryStub = (StatusManager_Intf) UnicastRemoteObject.exportObject(statMan, 0);
            System.out.println("Success: StatusManager exported to registry.");

            Naming.rebind("//" + host + ":" + port + "/StatusManager", registryStub);
            System.out.println("Success: StatusManager bound to reference.");
            System.out.println("Success: Cluster & StatusManager intialised.");
        } catch (Exception e) {
            System.out.println("FAILURE: Cluster.java: Error creating status manager");
            e.printStackTrace();
        } 
        
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
   
    /* Getters & Setters */

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
    public boolean test() {
        return true;
    }

}
