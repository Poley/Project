import java.rmi.*;

public interface Dispatcher_Intf extends Remote {

    public void registerToCluster(Node_Intf ni) throws java.rmi.RemoteException; 
    public void echoMessage(Node_Intf ni, String message) throws RemoteException;

}
