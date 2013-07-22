package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Controller {
    
    private Cluster cluster;
    private Dispatcher dispatch;

    private String host = "localhost";
    private short port = 1099;
    /* Needs to export and bind remote objects (dispatcher).
        */

    public Controller() {
        //dispatch = null; 
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager() );
            System.out.println("Success: Security Manager created.");
        } 
        
        cluster = new Cluster(host, port); // cluster exports and binds status manager

        try {
            dispatch = new Dispatcher(this);
            UnicastRemoteObject.unexportObject(dispatch, true);
            Dispatcher_Intf registryStub = (Dispatcher_Intf) UnicastRemoteObject.exportObject(dispatch, 0);
            System.out.println("Success: Dispatcher exported to registry.");
            
            Naming.rebind("//" + host + ":" + port + "/Dispatcher", registryStub);
            System.out.println("Success: Dispatcher bound to reference.");
            System.out.println("Success: PiManager created.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Controller.java: Error exporting dispatcher to registry.");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("FAILURE: Controller.java: URL binding the dispathcer is malformed.");
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
            System.out.println("1: Activate test methods.");
            System.out.println("2: Add IP and add to cluster.");
            System.out.println("0: Exit.");
            
            input = -1;
            try { input = Integer.parseInt(inputStream.readLine() ); } 
            catch (Exception e) { System.out.println("Error parsing input."); e.printStackTrace();  } 
            System.out.println("_________"); 

            switch(input) {
                case 1: System.out.println("Executing test methods.");
                        testComs(); 
                        break;
                case 2: System.out.print("Please enter the IP of the client you wish to register to the cluster (192.168.100.xxx): 192.168.100.");
                        try {
                            strInput = "192.168.100." + inputStream.readLine();
                        } catch (Exception e) {
                            System.out.println("\nError reading input.");
                            continue;
                        } 
                        System.out.println("Note: default port for accessing client is 1099.\n\nAttempting to register client at ip " + strInput + "...");
                        dispatch.addIPAndRegister(strInput);
                        continue;
                case 0: System.out.println("Exiting...");
                        System.exit(1);
                default:System.out.println("Error: Unrecognised Input.");
                        continue;
            } 
        } 
    } 

    public void executeAlgorithm() {
        dispatch.executeAlgorithm();
    }

    public boolean addClient(Client_Intf c) {
        return true;
        //return cluster.addClient(c);
    }
    
    public boolean removeClient(Client_Intf c) {
        return cluster.removeClient(c); 
    }

    public HashMap<Client_Intf, Pi> getClusterInfo() {
        return cluster.getFullDetails();
    }


    public void getClusterHistory() {

    }

    public void addIPAndRegister(String ip) {
        dispatch.addIPAndRegister(ip);
    } 

    /* ------- test methods -------- */
    public void testComs() {
        boolean algStat = dispatch.testRegistration(); 
        boolean taskRes = cluster.test(); 
            
        if (algStat && taskRes) System.out.println( "All basic test methods complete.");
        else System.out.println("Tests were NOT unanimously successful.");
    }
}
