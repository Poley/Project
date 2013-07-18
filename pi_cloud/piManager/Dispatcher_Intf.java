package pi_cloud;

import piClient.*;
import java.rmi.*;

public interface Dispatcher_Intf extends Remote{

    public boolean register(Client_Intf node) throws RemoteException;
    public boolean unRegister(Client_Intf node) throws RemoteException;
}
