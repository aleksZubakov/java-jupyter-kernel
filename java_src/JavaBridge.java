import py4j.GatewayServer;

public class JavaBridge {
    public static void main(String[] args) {

      GatewayServer gatewayServer = new GatewayServer(new JavaBridge(), Integer.parseInt(args[0]));

      gatewayServer.start();
      System.out.println("JVM for kernel started");
    }
}
