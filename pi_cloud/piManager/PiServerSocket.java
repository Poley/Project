package pi_cloud.piManager;

import java.util.ArrayList;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;

public class PiServerSocket extends WebSocketServer {

    ArrayList<WebSocket> connections = new ArrayList<WebSocket>();

    public PiServerSocket( int port) throws UnknownHostException {
        super( new InetSocketAddress( port) );
        System.out.println("PiServerSocket: Listening on port " + port);
        WebSocketImpl.DEBUG = true;
    } 

    public PiServerSocket( InetSocketAddress add) {
        super( add);
    } 
    
    public void onOpen( WebSocket conn, ClientHandshake handshake) {
        System.out.println( "Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has been made.");
        connections.add(conn);
    } 

    public void onClose( WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has closed: " + reason);
    } 

    public void onMessage( WebSocket conn, String message) {
        System.out.println("Client at " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " says: "+ message);
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
