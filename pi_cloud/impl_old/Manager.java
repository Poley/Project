import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Manager {

    public Manager () {}

    public static void main(String[] args) {
        
        Dispatcher dispatch = null;
        try {
            dispatch = new Dispatcher( (short) 1, 1);
            Dispatcher_Intf registryStub = (Dispatcher_Intf) UnicastRemoteObject.exportObject(dispatch, 1099);
            System.out.println( "Dispatcher Remote Object Successfully Created.");
        } catch (Exception e) {
            System.out.println( "Error creating dispatcher");
            e.printStackTrace();
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager( new RMISecurityManager() );
            System.out.println( "Security Manager successfully created.");
        }

        try {
            //Registry reg = LocateRegistry.createRegistry(1099);
            Naming.rebind( "//localhost:1099/Dispatcher", dispatch);
            System.out.println("Remote objects successfully bound to registry.");
            System.out.println("Dispatcher ready");
        } catch (Exception e) {
            System.out.println("Error binding remote objects to registry.");
            e.printStackTrace();
        }

    }
}
