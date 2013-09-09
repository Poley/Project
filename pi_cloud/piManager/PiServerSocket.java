package pi_cloud.piManager;

import java.util.ArrayList;
import java.util.HashMap;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

public class PiServerSocket extends WebSocketServer {

    ArrayList<WebSocket> connections = new ArrayList<WebSocket>();
    Controller controller = null;

    public PiServerSocket( int port) throws UnknownHostException {
        super( new InetSocketAddress( port) );
        System.out.println("PiServerSocket: Listening on port " + port);
        WebSocketImpl.DEBUG = true;
    } 

    public PiServerSocket( InetSocketAddress add, Controller c) {
        super( add);
        controller = c;
    } 
    
    public void onOpen( WebSocket conn, ClientHandshake handshake) {
        System.out.println( "Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has been made.");
        connections.add(conn);
    } 

    public void onClose( WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has closed: " + reason);
    } 

    public void onMessage( WebSocket conn, String message) {
        System.out.println("Recieved message: " + message);
        String[] splitlist = message.split("\\|");

        if (splitlist[0].contains("mergesort") ) {
            if (splitlist[1].equals("1") ){
                // skip checkin if distributed or not, assume it is for now.
                System.out.print("\nReceived List: ");
                String[] inputList = splitlist[3].split(",");

                int[] intlist = new int[inputList.length];
                for (int i = 0; i < inputList.length; i++) {
                    intlist[i] = Integer.parseInt(inputList[i] ); // PARSE DOUBLE!!!
                    System.out.print(intlist[i] + " ");
                } 
                System.out.print("\n");

                int[] result = controller.executeAlgorithm(intlist);
                String resultString = "" + result[0];
                for (int j=1; j<result.length; j++) { resultString += "," + result[j]; } 
                conn.send("mergesort|2|2|0|" + resultString.toString());
                System.out.println("Sent Message: " + "mergesort|2|2|0|" + resultString.toString());
            } 
        } else if (splitlist[0].contains("getClusterNetwork") ) { // Request received to return with a definition of the network / tree
            HashMap<String, String[]> network = controller.getClusterNetwork();
            String[] networkKeySet = network.keySet().toArray(new String[network.size()]);
            String networkMessage = "getClusterNetwork|2|";

            for (int i=0; i < networkKeySet.length; i++) {
                String[] children = network.get( networkKeySet[i]);
                
                networkMessage += networkKeySet[i]+":";
                if (children.length==2) networkMessage += children[0]+","+children[1]+"|";
                else if (children.length==1) networkMessage += children[0]+"|";
                else networkMessage += "|";
            } 
            // Message structure= "getClusterNetwork|2|rootHostname:childAhostname,childBHostname|childAhostname:childChostname|childBhostname:|childChostname:|
            //                             cmd|optcode|       node :    child     ,     child    |     node     :     child    |   leafNode   :|   leafNode   :|
            System.out.println(networkMessage);
            conn.send(networkMessage);
        } 

    } 

    public void onError( WebSocket conn, Exception ex) {
        ex.printStackTrace();
    } 

    
    // Close all established connections
    protected void closeConnections() {
        for(int i = 0; i < connections.size(); i++) {
            connections.get(i).close();
        } 
    } 


} 
