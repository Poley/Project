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

public class Controller {
    
    private Cluster cluster;
    private Dispatcher dispatch;

    private String host; 
    private short port = 1099;
    //private short clientPort = 1097;

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
        } catch (Exception e) { } 

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Success: Security Manager created.");
        } 

        try {
            Registry reg = LocateRegistry.createRegistry( port);
            cluster = new Cluster(host, port, reg); // cluster exports and binds status manager
            
            dispatch = new Dispatcher(this);
            UnicastRemoteObject.unexportObject(dispatch, true);
            Dispatcher_Intf registryStub = (Dispatcher_Intf) UnicastRemoteObject.exportObject(dispatch, port);
            System.out.println("Success: Dispatcher exported to registry.");
            
            //Naming.rebind("//" + host + ":" + port + "/Dispatcher", registryStub);
            reg.rebind("Dispatcher", registryStub);
            System.out.println("Success: Dispatcher bound to reference.");
            System.out.println("Success: PiManager created.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Controller.java: Error exporting dispatcher to registry.");
            e.printStackTrace();
        }
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
            System.out.println("4: Execute merge sort (and set children).");
            System.out.println("0: Exit.");
            
            input = -1;
            try { input = Integer.parseInt(inputStream.readLine() ); } 
            catch (Exception e) { System.out.println("Error parsing input."); e.printStackTrace();  } 
            System.out.println("_________"); 

            switch(input) {
                case 4: dispatch.defineClusterNetwork( cluster.getClients() ); // define each nodes children.
                        int[] mergeSortInput = {6,5,4,3,4,5,6,7,8,7,8,7,8,88,9,8,6,32,1,2,3,44,5,67,76,45,7,9,3,8,26,15,1,783,2,61,562,37,48,9,0,49,4};
                        System.out.println("Default list of integers will be used.");
                        dispatch.executeMergeSort(cluster.getClients(), mergeSortInput); 
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
                        System.exit(1);
                default:System.out.println("Error: Unrecognised Input.");
                        continue;
            } 
        } 
    } 

    public boolean addClient(Client_Intf c, String hostname) { return cluster.addClient(c, hostname); }
    public boolean removeClient(Client_Intf c) { return cluster.removeClient(c); }

    public HashMap<Client_Intf, Pi> getClusterInfo() { return cluster.getFullDetails(); }
    public void getClusterHistory() { }

    public String getHost() { return host; }

}
