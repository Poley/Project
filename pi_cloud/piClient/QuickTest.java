package pi_cloud.piClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class QuickTest {

	/**
	 * @param args
	 */
	private static short CPU = 0; // % CPU used.
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		executePs();
	}
	
	private static boolean executePs() { // Gets stats on process memory
        String line = "failure";
        String res[] = new String[11]; 
        String[] cmd = {"/bin/sh", "-c", "ps aux | grep java"}; // need the first two strings (running from shell) so that the pipe can be used, doesn't work otherwise.

        try {
            Process pr = Runtime.getRuntime().exec(cmd);
            InputStream is = pr.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            System.out.println("Output:");
            line = br.readLine();
            res = line.split("\\s+"); 
            for (int i = 0; i < res.length; i++) {
                System.out.println(i + ": " + res[i]);
                switch (i) {
                    case 3: CPU = (short) Float.parseFloat(res[i]); //System.out.println("Initial DRS: " + DRS);
                            continue;
//                    case 8: RSS = Integer.parseInt(res[i]);
//                            continue;
//                    case 9: PMEM = Short.parseShort(res[i]);
//                            continue;
                    default: continue;
                }
            } 

            System.out.println("%CPU: " + CPU);
//            System.out.println("RSS: " + RSS);
//            System.out.println("%MEM:" + PMEM);
        } catch (Exception e) {
            System.out.println("FAILURE: StatusMonitor.java: Error executing \"ps\" and acquiring DRS, RSS and %MEM");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
