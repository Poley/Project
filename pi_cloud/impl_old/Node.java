import java.rmi.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

public class Node implements Serializable, Node_Intf {

    String messages = "";
    String host;
    short port;

    public Node (String h, short p) throws RemoteException {
        super();
        host = h;
        port = p;
    }

    public static void main (String args[]) throws RemoteException, NotBoundException, MalformedURLException {
        Node_Intf ni = new Node("localhost", (short) 1099);

        try {
            Dispatcher_Intf dispatcher = (Dispatcher_Intf) Naming.lookup("rmi://localhost:1099/Dispatcher");
               
            while (true) { 
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("e: echo message\nr: register to dispatcher\nq: quit\n");
                System.out.print("Input: ");

                String input = inputStream.readLine();
                if (input.contains("e")) {
                    System.out.print("What do you wish to echo? "); 
                    dispatcher.echoMessage(ni, inputStream.readLine());
                } else if (input.contains("r") ) { 
                    dispatcher.registerToCluster(ni);
                } else if (input.contains("q") ) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
            System.out.println("Error finding bound remote file.");
        }
        
    }

    
    public void echoReply(String mess) throws RemoteException {
        System.out.println( mess);
    }

    public void verifyRegistration() throws RemoteException {
        System.out.println("You have been registered to the dispatcher.");
    }
    
    public String getHost() throws RemoteException {
        return host;
    }

    public short getPort() throws RemoteException {
        return port;
    }

}
