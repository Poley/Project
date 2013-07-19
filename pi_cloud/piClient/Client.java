package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;

public class Client extends UnicastRemoteObject implements Client_Intf, Serializable {

    private Dispatcher_Intf dispatch;
    private boolean busy;
    private StatusMonitor_Intf sm;

    private String host;
    private short port = 1079;
    
    public Client(String h, short p) throws RemoteException {
        host = h;
        //port = p;
        
        sm = new StatusMonitor();
        try { 
            UnicastRemoteObject.unexportObject(sm, true);
            StatusMonitor_Intf regStub = (StatusMonitor_Intf) UnicastRemoteObject.exportObject(sm, port);
            System.out.println("Status Monitor remote object has been successfully created.");

            Naming.rebind("//" + host + "/ClientStatusMonitor", regStub);
            System.out.println("Status Monitor has been successfully bound to registry.");
        } catch (Exception e) {
            System.out.println("Client.java Error in binding Status Monitor");
            e.printStackTrace();
        } 
    }

    public boolean executeAlgorithm() throws RemoteException {
        return true;
    }

    public StatusMonitor_Intf getStatusMonitor() throws RemoteException {
        return sm;
    }

    public boolean test() {
        return true;
    }

    // method managing dispatcher

}
