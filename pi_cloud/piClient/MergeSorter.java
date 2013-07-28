package pi_cloud.piClient;

import java.lang.Math;
import java.rmi.*;
import java.util.Arrays;

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
        int[] sortedList = new int[list.length];
        if (list.length > listThreshold && !(childA == null && childB == null) ){ // if (list is larger than threshold && children are available), split to children.
            int[] listA = Arrays.copyOfRange(list, 0, (list.length-1)/2);
            int[] listB = Arrays.copyOfRange(list, (list.length-1)/2, list.length-1);
            int[] sortedListA = childA.sort(listA);
            int[] sortedListB = childB.sort(listB);
            sortedList = merge(sortedListA, sortedListB);
        } else {
            sortedList = sortList( list);
        } 
        return sortedList;
    } 

    private int[] sortList(int[] list) {
        int[] listA = Arrays.copyOfRange(list, 0, (list.length-1)/2);
        int[] listB = Arrays.copyOfRange(list, (list.length-1)/2, list.length-1);
        
        if (list.length > 1) {
            return merge( sortList(listA), sortList(listB) );
        } else {
            return list;
        } 
    } 

    private int[] merge(int[] listA, int[] listB) {
        int[] sortedList = new int[listA.length + listB.length];
        int j = 0;
        for (int i = 0 ; i < (listA.length + listB.length - 2); i=i+2) {
            if (listA[i] < listB[i]) {
                sortedList[i] = listA[j];
                sortedList[i+1] = listB[j];
            } else {
                sortedList[i] = listB[j];
                sortedList[i+1] = listA[j];
            } 
            System.out.print("[");
            for (int k : sortedList) { System.out.print( k + ", "); }
            System.out.println("]");
            j++;
        } 
        return sortedList;
    } 


    // getters & setters
    public boolean setListThreshold(short t) throws RemoteException {
        listThreshold = t;
        return true;
    }
}
