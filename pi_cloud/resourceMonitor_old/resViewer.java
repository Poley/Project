import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.Process;

public class resViewer {

    public static void main(String[] args) {
        /*
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets) ) {
                displayInterfaceInformation(netint);
            }
        } catch (Exception e) { System.out.println( "babla"); }
        */
        Process p;
        try {
            p = new ProcessBuilder("/usr/bin/sudo", "-n", "/usr/sbin/tcpdump", "-i", "eth0").start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream() ));

            String tcpdumpOut = null;
            short tcpdumpLineCnt = 0;
            while ((tcpdumpOut = in.readLine()) != null ) {
                System.out.println(tcpdumpOut);
                tcpdumpLineCnt++;
                if (tcpdumpLineCnt > 10) break;
            }

            System.out.println("Output lines: " + tcpdumpLineCnt+"\nCommand exit code: " + p.exitValue());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        try {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                System.out.printf("InetAddress: %s\n", inetAddress);
            }
        } catch (Exception e) { System.out.println("Error getting interfaces"); }

        System.out.printf("\n");
    }

}
