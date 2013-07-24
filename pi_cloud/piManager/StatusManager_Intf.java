package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;

public interface StatusManager_Intf extends Remote {

    public boolean updateResourceDetails(Client_Intf node, short cpuUsage, int memUsage) throws RemoteException;
    public boolean updateTaskDetails(Client_Intf node, String task, String taskStatus, short ttc) throws RemoteException;
    
}
