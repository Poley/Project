package pi_cloud.piManager;

import java.util.ArrayList;
import java.util.HashMap;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

import com.google.gson.Gson;

/* This class represents a socket, and handles all socket communciation between Web Server and Pi Manager.
   It makes used of the java_websocket package, details of which can be found at: http://java-websocket.org/ 
 */
public class PiServerSocket extends WebSocketServer {

    private ArrayList<WebSocket> connections = new ArrayList<WebSocket>(); // Contains a list of open connections
    private Controller controller = null; 
    private WebSocket connection;

    public PiServerSocket( int port) throws UnknownHostException {
        super( new InetSocketAddress( port) );
        System.out.println("PiServerSocket: Listening on port " + port);
        WebSocketImpl.DEBUG = true;
    } 

    public PiServerSocket( InetSocketAddress add, Controller c) {
        super( add);
        controller = c;
    } 
    
    // Called when a connection is newly established
    public void onOpen( WebSocket conn, ClientHandshake handshake) {
        System.out.println( "Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has been made.");
        connections.add(conn);
    } 

    // Called when a socket connection closes
    public void onClose( WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has closed: " + reason);
    } 

    // Called when a message is received from the socket
    public void onMessage( WebSocket conn, String message) {
        System.out.println("Recieved message: " + message);
        String[] splitlist = message.split("\\|");

        connection = conn;

        if (splitlist[0].contains("mergesort") ) {
            if (splitlist[1].equals("1") ){
                // skipped checking if distributed or not, assume it is for now.
                String[] inputList = splitlist[3].split(",");

                int[] intlist = new int[inputList.length];
                for (int i = 0; i < inputList.length; i++) {
                    intlist[i] = Integer.parseInt(inputList[i] ); // PARSE DOUBLE!!!
                } 
                System.out.print("\n");

                controller.executeAlgorithm(intlist);
            } 
        } else if (splitlist[0].contains("getClusterNetwork") ) { // Request received to return with a definition of the network / tree
            HashMap<String, String[]> network = controller.getClusterNetwork();
            String jsonFile = writeTreeJson( network);
            String[] keySet = network.keySet().toArray(new String[network.size()] );
            for( int i=0; i < keySet.length; i++) {
                System.out.print("\n" + keySet[i] + ":");
                String[] children = controller.getClusterNetwork().get(keySet[i]) ;
                for (int j=0; j < children.length; j++) {
                    System.out.print( children[j] + ", ");
                } 
            } 
            System.out.println();
            
            String networkMessage = "getClusterNetwork|2|" + jsonFile;
            
            System.out.println("Send Message: "+ networkMessage);
            conn.send(networkMessage);
        } else if (splitlist[0].contains("eventData") && splitlist[1].contains("1") ) { // Recieved a request to send event data on most recent task execution
            String events = controller.getMostRecentTaskEvents();
            conn.send(events);
        } 

    } 

    // Called when there is an error across the socket channel.
    public void onError( WebSocket conn, Exception ex) {
        ex.printStackTrace();
    } 

    // Sends the result from a merge sort execution. This is done rather stupidly for the moment, sending the result to it's most recently established connection.
    protected void sendMergeSortResult(int[] result) {
        String resultString = "" + result[0];
        for (int j=1; j<result.length; j++) { resultString += "," + result[j]; } 
        connection.send("mergesort|2|2|0|" + resultString.toString());
        System.out.println("Sent Message: " + "mergesort|2|2|0|" + resultString.toString());
    } 

    
    // Close all established connections
    protected void closeConnections() {
        for(int i = 0; i < connections.size(); i++) {
            connections.get(i).close();
        } 
    } 

    // This method write a String object, of which defines the network in the format of a JSON file. When the web-server recieves this String, it writes it to a json file.
    private String writeTreeJson(HashMap<String, String[]> network ) {
        HashMap<String, JsonNode> nodes = new HashMap<String, JsonNode>();
        
        String[] networkKeySet = network.keySet().toArray(new String[network.size()]);
    
        JsonNode newNode;

        for (int i = 0; i < networkKeySet.length; i++) {
            if ( !nodes.containsKey(networkKeySet[i] )) {
                newNode = new JsonNode(networkKeySet[i] );
            } else {
                newNode = nodes.get( networkKeySet[i]);
            }
            
            String[] children = network.get( networkKeySet[i]);
            for (int j=0; j < children.length; j++) { // iterate through all children
                // create new JsonNode for the child if it doesn't exit
                JsonNode child;
                if ( !nodes.containsKey( children[j]) ) { 
                    child = new JsonNode( children[j]);
                } else {
                    child = nodes.get( children[j]);
                } 
                child.giveParent();
                newNode.addChild( child);
            } 
            nodes.put( networkKeySet[i], newNode);
        } 
        
        String[] nodeKeySet = nodes.keySet().toArray( new String[nodes.size()]);
        JsonNode root = null;
        for (int i=0; i < nodes.size(); i++) {
            if ( !nodes.get(nodeKeySet[i]).hasParent() ){
                System.out.print("\nRoot found!");
                root = nodes.get( nodeKeySet[i]);
                System.out.print( nodeKeySet[i] + "\n");
                break;
            } 
        } 

        Gson gson = new Gson(); // This package converts java objects to JSON files.
        String jsonMessage = gson.toJson(root);
        return jsonMessage; 
    } 


} 
