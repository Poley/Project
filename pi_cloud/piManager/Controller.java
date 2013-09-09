package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.*; // may not need this

import org.java_websocket.server.*;
import org.java_websocket.server.DefaultWebSocketServerFactory;

public class Controller {
    
    private Cluster cluster;
    private Dispatcher dispatch;
    private Connection dbConnection; // Connection to database

    private String host; 
    private short port = 1099;

    private PiServerSocket server = null;
    private PrintWriter outputRoute = null;
    private BufferedReader inputRoute = null;

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
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/pi_cloud" + "?user=piAdmin&password=pi_cloud");
            System.out.println("Success: Connection to database established.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Connection to database has failed.");
            System.exit(1);
        } 
        
        // Set up server socket for front-end to communicate with
        /*
        short socketPort = 4444;
        try {
            serverSocket = new ServerSocket(socketPort);
            System.out.println("Success: Server socket created.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Server Socket can't listen on port " + socketPort); 
            System.exit(1);
        } 
        */
        
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

    protected int[] executeAlgorithm(int[] mergeSortInput) {
        System.out.println("\nInput:") ;
        for (int i : mergeSortInput) System.out.print(" " + i);

        dispatch.defineClusterNetwork( cluster.getClients() ); // define each nodes children.
        int[] result = dispatch.executeMergeSort(cluster.getClients(), mergeSortInput); 

        System.out.println("\nSorted List: ");
        for (int j : result) System.out.print(" " + j);

        return result;
    } 

    public static void main(String args[]) {
        Controller c = new Controller();
        c.interact();
    }

    public void interact() {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        int input;

        String strInput;
        while (true) {
            System.out.println("\n_________\nAvailable actions:");
            System.out.println("1: View tasks at each client.");
            System.out.println("2: Request status update from clients."); 
            System.out.println("3: View Resource Stats at each client.");
            System.out.println("4: Execute (Distributed) merge sort."); 
            System.out.println("5: Execute (single device) merge sort.");
            System.out.println("6: Print event history.");
            System.out.println("0: Exit.");
            
            input = -1;
            try { input = Integer.parseInt(inputStream.readLine() ); } 
            catch (Exception e) { System.out.println("Error parsing input."); e.printStackTrace();  } 
            System.out.println("_________"); 

            switch(input) { 
                case 6: String eventStmtText = "SELECT * FROM Event";
                        try {
                            PreparedStatement eventPStmt = dbConnection.prepareStatement(eventStmtText);
                            ResultSet eventRs = eventPStmt.executeQuery();
                            
                            int row = 1;
                            while (eventRs.next() ) {
                                double taskId = eventRs.getDouble("task_id");
                                String taskStatus = eventRs.getString("status");
                                String detail = eventRs.getString("detail");
                                double timestamp = eventRs.getDouble("timestamp");
                                String ip = eventRs.getString("ip");
                                short pMem = eventRs.getShort("percentageMemory");
                                
                                System.out.println("\nROW " + row + ":\nTask ID:" + taskId + "\nTask Status: " + taskStatus + 
                                                   "\nDetail: " + detail + "\nTimestamp: "+ timestamp + 
                                                   "\nIP: " + ip + "\nPercentage Memory: " + pMem);
                            } 
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("FAILURE: Error executing query to database.");
                        } 


                case 5: dispatch.defineClusterNetwork( cluster.getClients() ); // define each nodes children.
                        int[] msInput = {6,5,4,3,4,5,6,7,8,7,8,7,8,88,9,8,6,32,1,2,3,44,5,67,76,45,7,9,3,8,26,15,1,783,2,61,562,37,48,9,0,49,4};
                        System.out.println("Default list of integers will be used.");
                        
                        Client_Intf clients[] = cluster.getClients();
                        try {
                            for (int i = 0; i < clients.length; i++) {
                                if ( !clients[i].getMS().hasChildren() ) { 
                                    int[] result = dispatch.executeMergeSort(cluster.getClients(), msInput);
                                    break;
                                }
                            } 
                        } catch (RemoteException e ) { e.printStackTrace(); }
                        
                        break;
                case 4: dispatch.defineClusterNetwork( cluster.getClients() ); // define each nodes children.
                        int[] mergeSortInput = {6,5,4,3,4,5,6,7,8,7,8,7,8,88,9,8,6,32,1,2,3,44,5,67,76,45,7,9,3,8,26,15,1,783,2,61,562,37,48,9,0,49,4};
                        System.out.println("Default list of integers will be used.");

                        System.out.println("\nInput:") ;
                        for (int i : mergeSortInput) System.out.print(" " + i);

                        int[] result = dispatch.executeMergeSort(cluster.getClients(), mergeSortInput); 

                        // Write to socket

                        System.out.println("\nSorted List: ");
                        for (int j : result) System.out.print(" " + j);
                        break;
                case 3: if (cluster.size() > 0) cluster.printResourceStats();
                        else System.out.println("Cluster is empty.");
                        break;
                case 2: cluster.requestUpdate();
                        System.out.println("Update request sent.");
                        break;
                case 1: System.out.println("Requesting current task from each client...\n");
                        if (cluster.size() > 0) cluster.printClusterTasks();
                        else System.out.println("Cluster is empty.");
                        break;
                case 0: System.out.println("Exiting...");
                        try {
                            //outputRoute.close();
                            //inputRoute.close();
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

    public HashMap<Client_Intf, Pi> getClusterInfo() { return cluster.getFullDetails(); }
    public void getClusterHistory() { }

    public String getHost() { return host; }

    /*
    private void setupSocketAndStreams() {
        try {
            Socket socket = new socket("localhost", 91); // ensure this port is free to use.
            System.out.println("Success: Server Socket created successfully.");

            outputRoute = new PrintWriter( serverSocket.getOutputStream(), true);
            inputRoute =  new BufferedReader( new InputStreamReader( serverSocket.getInputStream() ) );
            System.out.println("Success: Input and output streams to socket created successfully.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Error creating I/O to \"" + host + "\".");
        } 
        System.exit(1);
    } 
    */
}
