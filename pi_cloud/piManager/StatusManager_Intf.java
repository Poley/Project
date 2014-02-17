package pi_cloud.piManager;

import pi_cloud.piClient.*;
import java.rmi.*;

public interface StatusManager_Intf extends Remote {

    public boolean updateResourceDetails(Client_Intf node, short cpuUsage, int memUsage, int DRS, int RSS, short PMEM) throws RemoteException;
    public boolean updateTaskDetails(Client_Intf node, long taskId, String taskType, String taskStatus, String input, String output) throws RemoteException;
    
}
