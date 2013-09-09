package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Cluster {
    
    //private ArrayList<Pi> piCluster;
    private HashMap<Client_Intf, Pi> piCluster;
    private StatusManager statMan;
    private Connection dbConnection;

    protected Cluster(String host, short port, Registry reg, Connection dbc) {
        piCluster = new HashMap<Client_Intf, Pi>();
        System.out.println("Success: Cluster initialised.");
        dbConnection = dbc; 

        // creating status manager
        try { 
            statMan = new StatusManager(this);
        } catch (RemoteException e) { 
            System.out.println("FAILURE: Cluster.java: Error creating Status Manager."); 
            e.printStackTrace();
        }
        
    }

    // NOTE: Whenever a new client is added, all clients in cluster have their details updated. This is likely unnecessary.
    protected boolean addClient(Client_Intf n, String hn) {
        try { 
            Pi newP = new Pi (n.getStatusMonitor(), hn);
            statMan.setClientsStatusManager( newP.getStatusMonitor());
            piCluster.put(n, newP);

            System.out.println( "Cluster.java: Client at " + n.getHostname() + " added to cluster...\n____");
        } catch (Exception e) { 
            System.out.println( "FAILURE: Cluster.java: Error adding client to cluster.");
            e.printStackTrace(); 
            return false; 
        }
        
        //requestTaskDetailUpdate();
        return true;
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

    protected boolean updateResourceDetails(Client_Intf n, short cpuUsage, int memUsage, int DRS, int RSS, short PMEM) {
        Pi node = piCluster.get(n); // might be worth validating it exists
        node.updateResourceDetails(cpuUsage, memUsage, DRS, RSS, PMEM);
        piCluster.put(n, node);
        return true;
    }

    // Called when a client wishes to update the server's stored task details on itself.
    protected boolean updateTaskDetails(Client_Intf n, long taskId, String taskType, String taskStatus, int ttc) {
        // update Pi instance's details
        Pi node = piCluster.get(n);
        node.updateTaskDetails(taskId, taskType, taskStatus, ttc);
        piCluster.put(n, node); // is this needed?

        // Write task update to database (task_id, status, detail (list status), timestamp, ip, pMem)
        try {
            PreparedStatement addEvent = dbConnection.prepareStatement("INSERT INTO Event VALUES (?, ?, ?, ?, ?, ?)");
            addEvent.setLong(1, node.getTaskId());
            addEvent.setString(2, node.getTaskStatus() );
            addEvent.setString(3, ""); // list status
            addEvent.setLong(4, 1); // give timestamp
            addEvent.setString(5, node.getHost() ); // ip
            addEvent.setLong(6, node.getPMem() ); 

            int res = addEvent.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Cluster.java: Error writing to database.");
        } 

        return true;
    }
    
    // Triggers Status Manager to request all clients to update their task details and reply with the update.
    protected void requestUpdate() {
        statMan.requestUpdate( getStatusMonitors());
    }
    
    protected void printClusterTasks() {
        System.out.println("Host Name \t\tTask");

        Pi pis[] = piCluster.values().toArray( new Pi[piCluster.size()] ); 
        for (int i = 0; i < pis.length; i++) {
            System.out.println(pis[i].getHost() + "\t\t" + pis[i].getTaskType() );
        } 

        System.out.println("All tasks retrieved.");
    } 
    
    protected void printResourceStats() {
        Pi pis[] = piCluster.values().toArray( new Pi[piCluster.size()] ); 
        for (int i = 0; i < pis.length; i++) {
            System.out.println("HostName: " + pis[i].getHost() ); 
            pis[i].printResourceStats();
        }

        System.out.println("All tasks retrieved.");
    } 
    
    /* Getters & Setters */
    protected HashMap<Client_Intf, Pi> getFullDetails() {
        return piCluster;
    }

    protected String[] getClientHosts() {
        String[] hosts = new String[ piCluster.size()];
        Client_Intf clients[] = (Client_Intf[]) piCluster.keySet().toArray();
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = piCluster.get( clients[i]).getHost();
        }
        return hosts;
    } 

    protected StatusMonitor_Intf[] getStatusMonitors() {
        StatusMonitor_Intf statMons[] = new StatusMonitor_Intf[ piCluster.size()];
        Client_Intf clients[] = piCluster.keySet().toArray( new Client_Intf[piCluster.size()] );
        for (int i = 0; i < statMons.length; i++) {
            statMons[i] = piCluster.get(clients[i]).getStatusMonitor();
        }
        return statMons;
    }

    protected Client_Intf[] getClients() {
        return piCluster.keySet().toArray( new Client_Intf[piCluster.size()] );
    } 

    protected int size() {
        return piCluster.size();
    }

   
}
