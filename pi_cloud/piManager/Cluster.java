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

/* Object representing and containig all details on the Cluster of registered clients.
   Contains functions for adding/deleting clients, and updating details upon them.
 */
public class Cluster {
    
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

    // Updates the resource details of a specified Client.
    protected boolean updateResourceDetails(Client_Intf n, short cpuUsage, int memUsage, int DRS, int RSS, short PMEM) {
        Pi node = piCluster.get(n); // might be worth validating it exists
        node.updateResourceDetails(cpuUsage, memUsage, DRS, RSS, PMEM);
        piCluster.put(n, node);
        return true;
    }

    // Updates the task details of a specified Client.
    protected boolean updateTaskDetails(Client_Intf n, long taskId, String taskType, String taskStatus, String input, String output) {
        // update Pi instance's details
        Pi node = piCluster.get(n);
        node.updateTaskDetails(taskId, taskType, taskStatus, input, output); 
        piCluster.put(n, node);

        // Write task update to database (task_id, status, input, output, timestamp, ip, pMem, cpuUsage)
        // Adds a new 'Event' to the database.
        try {
            PreparedStatement addEvent = dbConnection.prepareStatement("INSERT INTO Event VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            addEvent.setLong(1, node.getTaskId());
            addEvent.setString(2, node.getTaskStatus() );
            addEvent.setString(3, input); // node input
            addEvent.setString(4, output); // node output
            addEvent.setLong(5, System.currentTimeMillis()); // give timestamp
            addEvent.setString(6, node.getHost() ); // ip
            addEvent.setLong(7, node.getPMem() );  // process memory
            addEvent.setLong(8, node.getCPUUsage() );  // cpu usage

            int res = addEvent.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Cluster.java: Error writing to database.");
            System.exit(1);
        } 

        return true;
    }
    
    // Getters
    
    // Triggers Status Manager to request all clients to update their task details and reply with the update.
    protected void requestUpdate() {
        statMan.requestUpdate( getStatusMonitors());
    }
    
    // Returns the a list of all StatusMonitors beloning to each registered client
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
