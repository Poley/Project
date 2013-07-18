import java.util.*;
import java.rmi.*;
import java.io.*;

public class Dispatcher extends java.rmi.server.UnicastRemoteObject implements Dispatcher_Intf {
    
    private ArrayList<Node_Intf> clients = new ArrayList<Node_Intf>(); // should be synchronized?

    public Dispatcher(short ac, int numNodes) throws RemoteException {
        super();
    }
    
    /*
    public static void main(String[] args) {

        while (true) {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.print( "a: Activate Objects. \ns: Send Message To Nodes.\nq: quit.\n\nInput: ");
            
            try {
            if (inputStream.readLine().contains("q") ) break;
            else if (inputStream.readLine().contains("a") ) activateNodes();
            else sendMessages("Testing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Used by available nodes to register to cluster where work will be divided */
    public void registerToCluster(Node_Intf ni) throws java.rmi.RemoteException {
         clients.add(ni);
         ni.verifyRegistration();
    }

    private boolean divideWorkload() { return true; }
    
    private static boolean activateNodes () { return true; }

    private static void sendMessages(String s) {
    
    }

    public void echoMessage(Node_Intf ni, String message) throws RemoteException {
        ni.echoReply("Received following message: " + message);
    }

}
