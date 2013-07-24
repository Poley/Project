import org.hyperic.sigar.cmd.*;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.CpuTimer;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Cpu;

public class testSigar {

    public testSigar() { } 
    
    public static void main(String args[]) {
        CpuInfo cpi = new CpuInfo();
        Sigar si = new Sigar();

        NetInfo ni = new NetInfo();
        try {
            ni.gather( si );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("hostname: " + ni.getHostName() );
        System.out.println("default gateway: " + ni.getDefaultGateway() );

        Cpu cp = new Cpu();

        try {
            cp.gather(si) ;
            cpi.output(args);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("Cpu User: " + CpuPerc.format( cp.getUser() ) );
        System.out.println("Cpu Sys: " + CpuPerc.format( cp.getSys() ) );

    } 
}
