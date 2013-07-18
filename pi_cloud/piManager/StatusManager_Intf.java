package pi_cloud;

import piClient.*;
import java.rmi.*;

public interface StatusManager_Intf extends Remote {

    public boolean updateResourceDetails(Client_Intf node, short cpuUsage, int memUsage) throws RemoteException;
    public boolean updateTaskDetails(Client_Intf node, String task, String taskStatus, int ttc) throws RemoteException;
    
}
