package pi_cloud.piClient;

import pi_cloud.piManager.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.io.Serializable;

public class Client extends UnicastRemoteObject implements Client_Intf, Serializable {

    private Dispatcher_Intf dispatch;
    private boolean busy;
    private StatusMonitor_Intf sm;

    private String host;
    private short statMonPort= 1098;
    
    public Client(String h, short p) throws RemoteException {
        host = h;
        //port = p;
        
        sm = new StatusMonitor();

        try {
            UnicastRemoteObject.unexportObject(sm, true);
            StatusMonitor_Intf regStub = (StatusMonitor_Intf) UnicastRemoteObject.exportObject(sm, statMonPort);
            System.out.println("Success: Status Monitor exported to registry.");
           
            try { Naming.unbind("//" + host + ":" + statMonPort + "/ClientStatusMonitor");
            } catch (Exception e) {}

            Naming.rebind("//" + host + ":" + statMonPort + "/ClientStatusMonitor", regStub);
            System.out.println("Success: Status Monitor bound to reference.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error exporting Status Monitor to registry.");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("FAILURE: Client.java: URL binding the Status Monitor object is malformed.");
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
