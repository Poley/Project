package pi_cloud;

import piClient.*;
import java.util.HashMap;
import java.rmi.*;

public class Controller {
    
    private Cluster cluster;
    private Dispatcher dispatch;

    public Controller() {
        cluster = new Cluster();
        try {
            dispatch = new Dispatcher(this);
        } catch (RemoteException e) {
            e.printStackTrace();
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
        boolean algStat = dispatch.test(); 
        boolean taskRes = cluster.test(); 
            
        if (algStat && taskRes) System.out.println( "All basic test methods complete.");
        else System.out.println("Tests were NOT unanimously successful.");
    }
}
