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
    private short port; 

    public Client(String h, short p) throws RemoteException {
        host = h;
        port = p;
        
        sm = new StatusMonitor();

        try {
            UnicastRemoteObject.unexportObject(sm, true);
            StatusMonitor_Intf regStub = (StatusMonitor_Intf) UnicastRemoteObject.exportObject(sm, port);
            System.out.println("Success: Status Monitor exported to registry.");
           
            try { Naming.unbind("//" + host + ":" + port + "/ClientStatusMonitor");
            } catch (NotBoundException e) {}

            Naming.rebind("//" + host + ":" + port + "/ClientStatusMonitor", regStub);
            sm = (StatusMonitor_Intf) Naming.lookup("//" + host + ":" + port + "/ClientStatusMonitor");
            System.out.println("Success: Status Monitor bound to reference.");
        } catch (RemoteException e) {
            System.out.println("FAILURE: Client.java: Error exporting Status Monitor to registry.");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("FAILURE: Client.java: URL binding the Status Monitor object is malformed.");
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("FAILURE: Client.java: Can't find status monitor in registry.");
        }

        System.out.println(host + " task = " + sm.getTask());
    }

    public void interact() {
        /*while (true) {
            System.out.println("Available Actions:");

        }
        */
    }
    public boolean executeAlgorithm() throws RemoteException {
        return true;
    }

    public StatusMonitor_Intf getStatusMonitor() throws RemoteException {
        System.out.println("Client.java: Retrieving status monitor.");
        return sm;
    }

    public boolean test() {
        return true;
    }

    /* Getters & Setters */
    public String getHost() {
        return host;
    } 


    // method managing dispatcher

}
