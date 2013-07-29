package pi_cloud.piClient;

import java.lang.Math;
import java.rmi.*;
import java.util.Arrays;
import java.lang.*;

public class MergeSorter implements MergeSorter_Intf {

    private MergeSorter_Intf childA = null;
    private MergeSorter_Intf childB = null;
    private short listThreshold = 1; // List size defining when the list should be distributed between children or should just be sorted

    public MergeSorter( MergeSorter_Intf a, MergeSorter_Intf b) {
        childA = a;
        childB = b;
    } 

    public boolean updateChildren( MergeSorter_Intf childA, MergeSorter_Intf child) throws RemoteException {
        return true;
    }

    public int[] sort(int[] list) throws RemoteException {
        System.out.println("Initial List:"); for (int i : list) { System.out.print(" " + i); } System.out.println();
        int[] listA, listB, sortedListA, sortedListB;

        int[] sortedList = new int[list.length];
        if (list.length > listThreshold && !(childA == null && childB == null) ){ // if (list is larger than threshold && children are available), split to children.
            int mid = list.length/2;
            listA = new int[mid];
            listB = new int[list.length-mid];
            System.arraycopy(list, 0, listA, 0, mid);
            System.arraycopy(list, mid, listB, 0, (list.length-mid));
            sortedListA = childA.sort(listA);
            sortedListB = childB.sort(listB);
            sortedList = merge(sortedListA, sortedListB);
        } else {
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
            if ( a > listA.length-1) {
                sortedList[i] = listB[b];
                b++;
            } else if (b > listB.length-1) {
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
    public boolean setListThreshold(short t) throws RemoteException {
        listThreshold = t;
        return true;
    }

    public boolean setChildren(MergeSorter_Intf ma, MergeSorter_Intf mb) throws RemoteException {
        childA = ma;
        childB = mb;
        return true;
    } 
}
