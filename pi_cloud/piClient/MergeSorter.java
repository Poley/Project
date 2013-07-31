package pi_cloud.piClient;

import java.lang.Math;
import java.rmi.*;
import java.util.Arrays;
import java.lang.*;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;

public class MergeSorter extends UnicastRemoteObject implements MergeSorter_Intf, Serializable {

    private MergeSorter_Intf childA = null;
    private MergeSorter_Intf childB = null;
    private short listThreshold = 5; // List size defining when the list should just be sorted locally rather than being distributed to children. 

    public MergeSorter( MergeSorter_Intf a, MergeSorter_Intf b) throws RemoteException {
        childA = a;
        childB = b;
    } 

    public int[] sort(int[] list) throws RemoteException {
        System.out.println("\nAsked to execute merge sort.\nList Size " + list.length + ". Has Children = " + hasChildren() + ".\n\nReceived List:"); 
        for (int i : list) { System.out.print(" " + i); } System.out.println();

        int[] listA, listB, sortedListA, sortedListB;
        int[] sortedList = new int[list.length];

        if (list.length > listThreshold && hasChildren() ) { // if (list is larger than threshold && children are available), split to children.
            System.out.println("\nDistributed to children.");
            int mid = list.length/2;
            listA = new int[mid];
            listB = new int[list.length-mid];

            // Split lists
            System.arraycopy(list, 0, listA, 0, mid);
            System.arraycopy(list, mid, listB, 0, (list.length-mid));

            // Send each half of a list to each child
            if (childB == null) {
                sortedListA = childA.sort(listA);
                sortedListB = sortList(listB);
                System.out.println("Merging local list + child list.");
            } else {
                sortedListA = childA.sort(listA);
                sortedListB = childB.sort(listB);
                System.out.println("Merging sorted lists from children...");
            } 
            
            // merge results from both children
            sortedList = merge(sortedListA, sortedListB); 
        } else {
            System.out.println("\nNo children available, sorting locally.");
            sortedList = sortList( list);
        } 
        
        System.out.println("\nSorted List"); for (int i : sortedList) System.out.print(" " + i); System.out.println();
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

    // getters & setters
    public void setListThreshold(short t) throws RemoteException { listThreshold = t; }
    
    public void setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException { childA = ma; childB = mb; } 
    public boolean hasChildren() throws RemoteException { return !(childA == null && childB == null); } 

}
