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

    public MergeSorter( MergeSorter_Intf a, MergeSorter_Intf b, String h, StatusMonitor sm) throws RemoteException {
        childA = a;
        childB = b;
        hostname = h;
        statMon = sm;
    } 

    public int[] sort(long taskID, int[] list) throws RemoteException {
        //System.out.println("\nAsked to execute merge sort.\nList Size " + list.length + ". Has Children = " + hasChildren() + ".\n\nReceived List:"); 
        System.out.print("\nReceived List: ");
        for (int i : list) { System.out.print(" " + i); } System.out.println();

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

            int mid = list.length/2;
            listA = new int[mid];
            listB = new int[list.length-mid];

            // Split lists
            System.arraycopy(list, 0, listA, 0, mid);
            System.arraycopy(list, mid, listB, 0, (list.length-mid));

            // Send each half of a list to each child
            if (childB == null) {
                sortedListA = childA.sort(taskID, listA);
                sortedListB = sortList(listB);
                System.out.println("Merging local list + child list.");
            } else {
                sortedListA = childA.sort(taskID, listA);
                sortedListB = childB.sort(taskID, listB);
                System.out.println("Merging sorted lists from children...");
            } 
            
            // merge results from both children
            try{
                statMon.setTaskStatus("Merging"); 
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 
            sortedList = merge(sortedListA, sortedListB); 
        } else {
            try{
                statMon.setTaskStatus("Sorting & merging locally"); 
                statMon.updateServer();
            } catch (Exception e) { e.printStackTrace(); } 
            sortedList = sortList( list);
        } 
        
        System.out.print("\nSorted List: "); for (int i : sortedList) System.out.print(" " + i); System.out.println();
        try {
            statMon.setTaskStatus("Done."); // "Idle" be better?
            statMon.setOutput( Arrays.toString(sortedList) );
            statMon.updateServer();
        } catch (Exception e) { e.printStackTrace(); } 
        return sortedList;
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
        } else if (childA != null ^ childB != null) {
            childrenString = new String[1];
            if (childA != null) childrenString[0] = childA.getHostname();
            else childrenString[0] = childB.getHostname();
        }
        return childrenString;
    } 

    // getters & setters
    public String getHostname() throws RemoteException { return hostname; }
    public void setListThreshold(short t) throws RemoteException { listThreshold = t; }
    
    public void setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException { childA = ma; childB = mb; } 
    public boolean hasChildren() throws RemoteException { return !(childA == null && childB == null); } 

}
