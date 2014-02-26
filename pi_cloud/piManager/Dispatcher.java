package pi_cloud.piManager;
import pi_cloud.piClient.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

/* This class handles the cluster of clients, handling functions such as registration,
    defining and acquiring the network stucture of the cluster. This object is used as a remote object client-side.
 */
public class Dispatcher extends UnicastRemoteObject implements Dispatcher_Intf {
    private Controller c;
    private ArrayList<String> ipAddresses = new ArrayList<String>();

    public Dispatcher(Controller contr) throws RemoteException {
        super(); 
        c = contr;
    }

    public boolean register(Client_Intf node, String hostname) throws RemoteException {
        try {
            System.out.println("___\nDispatcher.java: Registration request recieved from " + node.getHostname() + ".");
        } catch (Exception e) {
            System.out.println("Dispatcher.java: Error calling node.getHost() server-side.");
            e.printStackTrace();
        }
        return c.addClient(node, hostname); 
    }

    public boolean unRegister(Client_Intf node) throws RemoteException {
        return c.removeClient(node);
    }

    protected void executeMergeSort(long taskID, Client_Intf[] clients, int[] input) {
        int[] sorted = new int[input.length];

        try {
            if (clients.length > 0) {
            	long start = System.currentTimeMillis();
                clients[0].executeMergeSort(taskID, input); // The first client in the list is always treated as the root.
                long timeTaken = start - System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        String taskCreateStr = "INSERT INTO Task (task_id, type) VALUES ('" + taskID + "', 'MergeSort');";
        // Connecting to database
        Connection dbConnection = null;
        try {
            // Connects to the database named "pi_cloud" on the local server.
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/piCloud" + "?user=piAdmin&password=pi_cloud");
            System.out.println("Success: Connection to database established.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Connection to database has failed.");
            System.exit(1);
        } 
        try {
            PreparedStatement createTaskStmt = dbConnection.prepareStatement(taskCreateStr);
            createTaskStmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        
        
   }

    public void mergeSortResult(int[] result) throws RemoteException {
        c.mergeSortResult(result);    
    } 

    // Define children within the network, used pre-execution of certain algorithms.
    protected void defineClusterNetwork(Client_Intf[] clients, int listLength) {
        System.out.println("Clients available = " + clients.length);
        int i; // iterates across all nodes, and allocates them children if any are available.
        int j; // marks the next node to be allocated as a child.
        System.out.println( clients);
        for (i = 0, j=1; j < clients.length; i++) {
           try {
               if ( ((clients.length) - j) > 1) { // two children avilable to be assigned
                    clients[i].getMS().setChildren( clients[j].getMS(), clients[j+1].getMS() );
                    j+=2;
                } else if ( ((clients.length)-j) == 1 ) { // only one child available to be assigned
                    clients[i].getMS().setChildren( clients[j].getMS(), null);
                    j++;
                } 
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("FAILURE: Dispatcher.java: Error connecting to client MergeSorter.");
            } 
        } 
    }

    protected HashMap<String, String[]> getClusterNetwork(Client_Intf[] clients) {
        HashMap<String, String[]> clusterNetwork = new HashMap<String, String[]>();
        
        try {    
            for (int i = 0; i < clients.length; i++) {
                String[] childHostnames = clients[i].getMSChildHostnames();
                clusterNetwork.put(clients[i].getHostname(), childHostnames);
            } 
        } catch (RemoteException e) {
            e.printStackTrace();
        } 
        return clusterNetwork;
    } 

    /* Getters & Setters */
    public String getHost() throws RemoteException { return c.getHost(); } 

}
