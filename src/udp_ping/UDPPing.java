package udp_ping;
/**
 *
 * @author  cli
 * @version 
 */
public class UDPPing extends Object {
    InnerClass zzzi;
    
    /** Creates new UDPPing */
    public UDPPing() {
        zzzi = new InnerClass();
    }

    class InnerClass{
    int i;
    public InnerClass(){
        i=0;
    }
    };

    public void startThreads(String args[]){
        String IP      = args[0];
        String Port    = args[1];
        long id = System.currentTimeMillis();
        
        
        if(args.length == 2){
            DatagramSender sender = new DatagramSender();
            try{
                sender.initialize(id, IP, Port, 128);
            }catch(Exception e){
                e.printStackTrace();
                System.exit(13);
            }
            sender.start();
        }else if(args[2].equals("echo")){
            DatagramReflector reflector = new DatagramReflector();
            try{
                reflector.initialize(id, IP, Port, 128);
            }catch(Exception e){
                e.printStackTrace();
                System.exit(12);
            }
            reflector.start();
        }
    }
    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        if(args.length < 2 || args.length > 3){
            System.out.println("usage:");
            System.out.println("java UDPPing targetIP targetPort [echo]");
            System.out.println();
        }else{
            UDPPing pinger = new UDPPing();
            pinger.startThreads(args);
        }
    }

}
