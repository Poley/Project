import java.rmi.*;

public interface Node_Intf extends Remote {

    //public void callback(String s) throws RemoteException;
    public String getHost() throws RemoteException;
    public short getPort() throws RemoteException;

    public void echoReply(String mess) throws RemoteException;
    public void verifyRegistration() throws RemoteException;
}
