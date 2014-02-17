package pi_cloud.piClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QuickTest {

	/**
	 * @param args
	 */
	private static short CPU = 0; // % CPU used.
	private static Connection dbConnection;
	
	public static void main(String[] args) {
		try {
            // Connects to the database named "pi_cloud" on the local server.
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/piCloud" + "?user=piAdmin&password=pi_cloud");
            System.out.println("Success: Connection to database established.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("FAILURE: Connection to database has failed.");
            System.exit(1);
        } 
		String x = getMostRecentTaskIDs();
	}
	
	public static String getMostRecentTaskIDs() {
        String eventQry = "SELECT task_id " +
                             "FROM Task " + // Gets task_id of most recently executed task
                             "ORDER BY task_id DESC " +
                             "LIMIT 10";
        String recentTasks = "recentTasks|2";
        try {
            PreparedStatement eventStmt = dbConnection.prepareStatement(eventQry);
            ResultSet eventRs = eventStmt.executeQuery();

            while (eventRs.next()) {
            	recentTasks += "|" + eventRs.getDouble("task_id");
            } 
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Controller.java: Error executing query to retrieve most recent task events.");
            System.exit(1);
        } 

        System.out.println(recentTasks);
        return recentTasks;
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
