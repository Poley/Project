package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;

public interface Dispatcher_Intf extends Remote{

    public boolean register(Client_Intf node, String hostname) throws RemoteException;
    public boolean unRegister(Client_Intf node) throws RemoteException;
    public String getHost() throws RemoteException;
}
