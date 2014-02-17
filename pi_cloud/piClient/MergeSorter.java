package pi_cloud.piClient;

import java.lang.Math;
import java.rmi.*;
import java.util.Arrays;
import java.lang.*;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;

public class MergeSorter extends UnicastRemoteObject implements MergeSorter_Intf, Serializable {

    private String hostname;
    private MergeSorter_Intf childA = null;
    private MergeSorter_Intf childB = null;
    private short listThreshold = 2; // List size defining when the list should just be sorted locally rather than being distributed to children. 

    private StatusMonitor statMon = null;

    private short resultsReceived; // Keeps track of how many children have responded
    private int[] cachedResult = new int[0]; // When two children are given a part of a list, the first result to respond is cached so as to be used later when the 2nd child responds.
    private MergeSorter_Intf parent;
    private Client client; // The client is only known by the root node, otherwise remains null.
    private boolean isRoot = false;

    public MergeSorter( MergeSorter_Intf a, MergeSorter_Intf b, String h, StatusMonitor sm) throws RemoteException {
        childA = a;
        childB = b;
        hostname = h;
        statMon = sm;
    } 
    
    public void sort(Client cli, MergeSorter_Intf parentNode, long taskID, int[] list, boolean isR) throws RemoteException {
       // refreshDefaultValues();
        parent = parentNode;
        isRoot = isR;
        if (isRoot) client = cli;
        
        statMon.refreshDefaultValues(); // Means the next execution won't contain old values
        
        System.out.print("\nReceived List: ");
        for (int i : list) { System.out.print(" " + i); } System.out.println();

        System.out.println("Children:" + childA + ", " + childB);
        try {
            statMon.setTaskID( taskID);
            statMon.setTaskType("Merge sort");
            statMon.setInput( Arrays.toString(list) );
            statMon.setTaskStatus("Recieved input");
            statMon.updateServer();
        } catch (Exception e) { e.printStackTrace(); } 

        int[] listA, listB, sortedListA, sortedListB;
        int[] sortedList = new int[list.length];

        if (list.length > listThreshold && hasChildren() ) { // if (list is larger than threshold && children are available), split to children.
            System.out.println("\nDistributing to children.");
            try {
                statMon.setTaskStatus("Waiting for children reply"); 
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 

            System.out.println("\nu wot m8.");
            
            int mid = list.length/2;
            listA = new int[mid];
            listB = new int[list.length-mid];

            // Split lists
            System.arraycopy(list, 0, listA, 0, mid);
            System.arraycopy(list, mid, listB, 0, (list.length-mid));

            // Send each half of a list to each child
            // NOTE: It is here where the issue for the synchronous execution lies.
            if (childB == null) {
                cachedResult = sortList(listB);
                resultsReceived++; // indicates that the client will wait for only one callback before it replies to it's parent. 
            } else {
                childA.sort(null, this, taskID, listA, false);
                childB.sort(null, this, taskID, listB, false);
            } 
        } else {
            try {
                statMon.setTaskStatus("Sorting locally"); 
                System.out.println("Sorting locally");
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 
            sortedList = sortList( list);
            resultsReceived++;
            sortingCallback( sortedList);
        } 
        
    } 

    private int[] sortList(int[] list) {
        int size = list.length;
        int mid = list.length / 2;
        if ( size > 1) {
            int[] listA = new int[mid];
            int[] listB = new int[size-mid];
            System.arraycopy(list, 0, listA, 0, mid); // Copy left half into listA
            System.arraycopy(list, mid, listB, 0, size-mid ); // Copy right half into listB
            return merge( sortList(listA), sortList(listB) );
        } else {
            return list; // return list with single value
        } 
    } 

    private int[] merge(int[] listA, int[] listB) {
        int[] sortedList = new int[listA.length + listB.length];
        int a = 0, b = 0;

        for (int i = 0; i <= sortedList.length-1; i++) {
            if ( a > listA.length-1) { // if listA is empty
                sortedList[i] = listB[b];
                b++;
            } else if (b > listB.length-1) { // if listB is empty
                sortedList[i] = listA[a];
                a++;
            } else {
                if (listA[a] < listB[b]) {
                    sortedList[i] = listA[a];
                    a++;
                } else {
                    sortedList[i] = listB[b];
                    b++;
                } 
            } 
        } 
        return sortedList;
    } 

    public String[] getChildHostnames() throws RemoteException{
        String[] childrenString = new String[0];

        if (childA != null && childB != null) {
            childrenString = new String[2];
            childrenString[0] = childA.getHostname();
            childrenString[1] = childB.getHostname();
        } else if (childA != null | childB != null) {
            childrenString = new String[1];
            if (childA != null) childrenString[0] = childA.getHostname();
            else childrenString[0] = childB.getHostname();
        }
        return childrenString;
    } 

    // When a child has sorted it's list, it calls this method to reply to it's parent with the answer.
    public synchronized void sortingCallback(int[] result) throws RemoteException {
        resultsReceived++;
        if (resultsReceived == 2) {
            try {
                statMon.setTaskStatus("Merging"); 
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 
            int[] sortedList = merge( result, cachedResult); 
            
            System.out.print("\nSorted List: "); for (int i : sortedList) System.out.print(" " + i); System.out.println();
            try {
                statMon.setTaskStatus("Done."); // "Idle" be better?
                statMon.setOutput( Arrays.toString(sortedList) );
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 
            // reply to parent
            
            if (!isRoot) parent.sortingCallback( sortedList);
            else client.taskFinished(sortedList);
            resultsReceived = 0;
        } else {
            cachedResult = result;
        } 
    } 

    private void refreshDefaultValues() {
        childA = null;
        childB = null;
        resultsReceived = 0;
        cachedResult = new int[0];
        parent = null;
        client = null;
        isRoot = false;
    } 

    // getters & setters
    public String getHostname() throws RemoteException { return hostname; }
    public void setListThreshold(short t) throws RemoteException { listThreshold = t; }
    
    public void setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException { 
        System.out.println("I've been given children!");
        childA = ma;
        childB = mb; 
    } 
    public boolean hasChildren() throws RemoteException { return !(childA == null && childB == null); } 

}
