package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

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
            System.out.println("Security Manager successfully created.");
        } 
        
        cluster = new Cluster(host, port); // cluster exports and binds status manager

        try {
            dispatch = new Dispatcher(this);
            UnicastRemoteObject.unexportObject(dispatch, true);
            Dispatcher_Intf registryStub = (Dispatcher_Intf) UnicastRemoteObject.exportObject(dispatch, 0);
            System.out.println("Dispatcher remote object created successfully.");

            Naming.rebind("//" + host + ":" + port + "/Dispatcher", registryStub);
            System.out.println("Dispatcher successfully bound to server registry.");
            System.out.println("PiManager successfully created.");
        } catch (Exception e) {
            System.out.println("Error in binding dispatcher.");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Controller c = new Controller();
        c.interact();
    }

    public void interact() {

        while (true) {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Available actions:");
            System.out.println("1: Activate test methods.");
            System.out.println("0: Exit.");

            int input = -1;
            try { input = Integer.parseInt(inputStream.readLine() ); } 
            catch (Exception e) { System.out.println("Error parsing input."); e.printStackTrace();  } 

            switch(input) {
                case 1: System.out.println("Executing test methods.");
                        testComs(); 
                        break;
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
        return cluster.addClient(c);
    }
    
    public boolean removeClient(Client_Intf c) {
        return cluster.removeClient(c); 
    }

    public HashMap<Client_Intf, Pi> getClusterInfo() {
        return cluster.getFullDetails();
    }

    public void getClusterHistory() {

    }


    /* ------- test methods -------- */
    public void testComs() {
        boolean algStat = dispatch.testRegistration(); 
        boolean taskRes = cluster.test(); 
            
        if (algStat && taskRes) System.out.println( "All basic test methods complete.");
        else System.out.println("Tests were NOT unanimously successful.");
    }
}
