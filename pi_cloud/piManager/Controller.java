package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.*; // may not need this

import org.java_websocket.server.*;

/* This class co-ordinates communcation between the web server and all clients.
   It sets up the database, RMI objects and web socket communcation. 
   Run this class to start the Pi Manager.
 */
public class Controller {
    
    private Cluster cluster;
    private Dispatcher dispatch;
    private Connection dbConnection; // Connection to database

    private String host; 
    private short port = 1099;

    private PiServerSocket server = null;

    public Controller() {
        // Finding local IP address
        try {
            NetworkInterface ni = NetworkInterface.getByName("eth0");
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

            while (inetAddresses.hasMoreElements() ) {
                InetAddress ia = inetAddresses.nextElement();
                if(!ia.isLinkLocalAddress() ) {
                    host = ia.getHostAddress();
                    System.out.println("Local IP Address: " + host); 
                } 
            } 
        } catch (Exception e) {
            System.out.println("FAILURE: Error acquiring local ip address.");
            e.printStackTrace();
        } 

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Success: RMI Security Manager created.");
        } 
        
        // Connecting to database
        try {
            // Connects to the database named "pi_cloud" on the local server.
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/piCloud" + "?user=piAdmin&password=pi_cloud");
            System.out.println("Success: Connection to database established.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Connection to database has failed.");
            System.exit(1);
        } 
        
        // Setting up RMI
        try {
            Registry reg = LocateRegistry.createRegistry( port);
            cluster = new Cluster(host, port, reg, dbConnection); // cluster exports and binds status manager
            
            dispatch = new Dispatcher(this);
            UnicastRemoteObject.unexportObject(dispatch, true);
            Dispatcher_Intf registryStub = (Dispatcher_Intf) UnicastRemoteObject.exportObject(dispatch, port);
            System.out.println("Success: Dispatcher exported to RMI registry.");
            
            //Naming.rebind("//" + host + ":" + port + "/Dispatcher", registryStub);
            reg.rebind("Dispatcher", registryStub);
            System.out.println("Success: Dispatcher bound to RMI object reference.");
            System.out.println("Success: PiManager created.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Controller.java: Error exporting dispatcher to registry.");
            e.printStackTrace();
        }

        connectAndListenToPort();
    }
    
    public static void main(String args[]) {
        Controller c = new Controller();
        c.interact();
    }

    // Sets up the socket which the web server can then connect to.
    public void connectAndListenToPort() {
        try {
            server = new PiServerSocket( new InetSocketAddress("localhost", 4444), this );
            System.out.println( server.getAddress() );
            
            server.setWebSocketFactory( new DefaultWebSocketServerFactory() );
            server.start();
        } catch (Exception e) { 
            e.printStackTrace();
        }
    } 

    // Initiates the execution of a merge sort algorithm
    protected void executeAlgorithm(int[] mergeSortInput) {
        dispatch.defineClusterNetwork( cluster.getClients() , mergeSortInput.length ); // define each nodes children.

        // Create Task in database
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		long taskID = System.currentTimeMillis();
		Date x = new Date(taskID);
		String y = df.format(x);
		long timeID = Long.parseLong(y);
        String taskCreateStr = "INSERT INTO Task (task_id, type) VALUES ('" + timeID + "', 'MergeSort');";
        try {
            PreparedStatement createTaskStmt = dbConnection.prepareStatement(taskCreateStr);
            createTaskStmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        dispatch.executeMergeSort(timeID, cluster.getClients(), mergeSortInput); 
    } 

    // Called when a merge sort's result has been acquired.
    protected void mergeSortResult(int[] result) {
    	long taskID = 0;
    	String getTaskID = "SELECT task_id FROM Task WHERE task_id >= ALL (SELECT task_id FROM Task)";
    	try {
			PreparedStatement idStatement = dbConnection.prepareStatement(getTaskID);
			ResultSet bleh = idStatement.executeQuery();
			
			while(bleh.next()){
				taskID = bleh.getLong("task_id");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	String inputString = "[ " + result[0];
        
        for (int i=1; i<result.length ; i++){
        	inputString += ", " + result[i];
        }
        
        inputString += " ]";
    	
    	String taskCreateStr = "UPDATE Task SET output = '" + inputString + "' WHERE task_id = " + taskID;
        try {
        	PreparedStatement taskStmt = dbConnection.prepareStatement(taskCreateStr);
			taskStmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("FAILURE: SQLException");
			e.printStackTrace();
		}
        server.sendMergeSortResult(result);
    } 
    
    // Retrieves the set of events that have occurred in a specific task.
    public String getPreviousTaskEvents(String id){
    	double taskID = Double.parseDouble(id);
    	
    	String eventQry = "SELECT * FROM Event e " +
    			"INNER JOIN ( SELECT task_id AS ti FROM Task WHERE task_id = ?) m " + // ? is the task id passed as a parameter
    			"ON e.task_id = m.ti";
    	
    	String taskQry = "SELECT * FROM Task " +
                "WHERE task_id = " + taskID;
    	String eventsMessage = "eventData|2";
    	try {
        	PreparedStatement taskStmt = dbConnection.prepareStatement(taskQry);
            ResultSet taskRs = taskStmt.executeQuery();
            
            while (taskRs.next()){
            	eventsMessage += "|" + taskRs.getLong("task_id") + "|";
            	eventsMessage += taskRs.getString("type") + "|";
                eventsMessage += taskRs.getString("input") + "|";
                eventsMessage += taskRs.getString("output") + "|";
                eventsMessage += taskRs.getLong("timetaken");
            }
            
    		PreparedStatement eventStmt = dbConnection.prepareStatement(eventQry);
    		eventStmt.setDouble(1,  taskID);
    		ResultSet eventRs = eventStmt.executeQuery();

    		while (eventRs.next()) {
    			eventsMessage += "|" + eventRs.getLong("task_id") + "|";
    			eventsMessage += eventRs.getString("status") + "|";
    			eventsMessage += eventRs.getString("input") + "|";
    			eventsMessage += eventRs.getString("output") + "|";
    			eventsMessage += eventRs.getLong("timestamp") + "|";
    			eventsMessage += eventRs.getString("ip") + "|";
    			eventsMessage += eventRs.getShort("percentageMemory") + "|";
    			eventsMessage += eventRs.getShort("cpuUsage");
    		} 
    	} catch (SQLException e) {
    		e.printStackTrace();
    		System.out.println("Controller.java: Error executing query to retrieve task " + id);
    		System.exit(1);
    	} 

    	System.out.println(eventsMessage);
    	return eventsMessage;
    }

    // Retrieves the set of events that have occurred in the most recent task.
    public String getMostRecentTaskEvents() {
        String eventQry = "SELECT * FROM Event e " +
                             "INNER JOIN ( SELECT max(task_id) ti FROM Task) m " + // Gets task_id of most recently executed task
                             "ON e.task_id = m.ti";
        String taskQry = "SELECT * FROM Task t " +
                "INNER JOIN ( SELECT max(task_id) ti FROM Task) m " + // Gets task_id of most recently executed task
                "ON t.task_id = m.ti";
        String eventsMessage = "eventData|2";
        try {
        	PreparedStatement taskStmt = dbConnection.prepareStatement(taskQry);
            ResultSet taskRs = taskStmt.executeQuery();
            
            while (taskRs.next()){
            	eventsMessage += "|" + taskRs.getLong("task_id") + "|";
            	eventsMessage += taskRs.getString("type") + "|";
                eventsMessage += taskRs.getString("input") + "|";
                eventsMessage += taskRs.getString("output") + "|";
                eventsMessage += taskRs.getLong("timetaken");
            }
            
            PreparedStatement eventStmt = dbConnection.prepareStatement(eventQry);
            ResultSet eventRs = eventStmt.executeQuery();

            while (eventRs.next()) {
                eventsMessage += "|"+ eventRs.getLong("task_id") + "|";
                eventsMessage += eventRs.getString("status") + "|";
                eventsMessage += eventRs.getString("input") + "|";
                eventsMessage += eventRs.getString("output") + "|";
                eventsMessage += eventRs.getLong("timestamp") + "|";
                eventsMessage += eventRs.getString("ip") + "|";
                eventsMessage += eventRs.getShort("percentageMemory") + "|";
    			eventsMessage += eventRs.getShort("cpuUsage");
            } 
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Controller.java: Error executing query to retrieve most recent task events.");
            System.exit(1);
        } 

        System.out.println(eventsMessage);
        return eventsMessage;
    } 

    //Gets the 10 most recent task IDs
    public String getMostRecentTaskIDs() {
        String eventQry = "SELECT task_id " +
                             "FROM Task " + 
                             "ORDER BY task_id DESC " +
                             "LIMIT 10";
        String recentTasks = "recentTasks|2";
        try {
            PreparedStatement eventStmt = dbConnection.prepareStatement(eventQry);
            ResultSet eventRs = eventStmt.executeQuery();

            while (eventRs.next()) {
            	recentTasks += "|" + eventRs.getLong("task_id");
            } 
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Controller.java: Error executing query to retrieve most recent task events.");
            System.exit(1);
        } 

        System.out.println(recentTasks);
        return recentTasks;
    } 
    // Very simple interface for interaction, can be useful when testing out new algorithms as it will not longer require interaction with the web app.
    public void interact() {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        int input;

        String strInput;
        while (true) {
            System.out.println("\n_________\nAvailable actions:");
            System.out.println("0: Exit.");
            
            input = -1;
            try { input = Integer.parseInt(inputStream.readLine() ); } 
            catch (Exception e) { System.out.println("Error parsing input."); e.printStackTrace();  } 
            System.out.println("_________"); 

            switch(input) { 
                case 0: System.out.println("Exiting...");
                        try {
                            server.closeConnections();
                        } catch (Exception e) { 
                            e.printStackTrace(); 
                            System.out.println("FAILURE: Error closing socket and streams."); 
                        }
                        System.exit(1);
                default:System.out.println("Error: Unrecognised Input.");
                        continue;
            } 
        } 
    } 

    public HashMap<String, String[]> getClusterNetwork() {
        return dispatch.getClusterNetwork(cluster.getClients() );
    } 

    public boolean addClient(Client_Intf c, String hostname) { return cluster.addClient(c, hostname); }
    public boolean removeClient(Client_Intf c) { return cluster.removeClient(c); }

    public String getHost() { return host; }
}
