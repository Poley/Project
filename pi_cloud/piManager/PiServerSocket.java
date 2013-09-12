package pi_cloud.piManager;

import java.util.ArrayList;
import java.util.HashMap;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

import com.google.gson.Gson;

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
                String[] inputList = splitlist[3].split(",");

                int[] intlist = new int[inputList.length];
                for (int i = 0; i < inputList.length; i++) {
                    intlist[i] = Integer.parseInt(inputList[i] ); // PARSE DOUBLE!!!
                } 
                System.out.print("\n");

                int[] result = controller.executeAlgorithm(intlist);
                String resultString = "" + result[0];
                for (int j=1; j<result.length; j++) { resultString += "," + result[j]; } 
                conn.send("mergesort|2|2|0|" + resultString.toString());
                System.out.println("Sent Message: " + "mergesort|2|2|0|" + resultString.toString());
            } 
        } else if (splitlist[0].contains("getClusterNetwork") ) { // Request received to return with a definition of the network / tree
            HashMap<String, String[]> testNetwork = new HashMap<String, String[]>();
            //testNetwork.put("nodeA", new String[]{"nodeB", "nodeC"});
            //testNetwork.put("nodeB", new String[]{"nodeD", "nodeE"});
            //testNetwork.put("nodeC", new String[]{"nodeF"});
            //testNetwork.put("nodeD", new String[]{});
            //testNetwork.put("nodeE", new String[]{});
            //testNetwork.put("nodeF", new String[]{});
            
            String jsonFile = writeTreeJson( controller.getClusterNetwork() );
            String[] keySet = controller.getClusterNetwork().keySet().toArray(new String[testNetwork.size()] );
            for( int i=0; i < keySet.length; i++) {
                System.out.println( keySet[i] + ":");
                String[] children = controller.getClusterNetwork().get(keySet[i]) ;
                for (int j=0; j < children.length; j++) {
                    System.out.println( children[j]);
                } 
            } 
            
            ///String jsonFile = writeTreeJson( testNetwork );

            String networkMessage = "getClusterNetwork|2|" + jsonFile;
            
            System.out.println("Send Message: "+ networkMessage);
            conn.send(networkMessage);
        } else if (splitlist[0].contains("eventData") && splitlist[1].contains("1") ) { // Recieved a request to send event data on most recent task execution
            String events = controller.getMostRecentTaskEvents();
            conn.send(events);
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
                System.out.println("Root found!");
                root = nodes.get( nodeKeySet[i]);
                break;
            } 
        } 

        Gson gson = new Gson();
        String jsonMessage = gson.toJson(root);
        return jsonMessage; 
    } 


} 
