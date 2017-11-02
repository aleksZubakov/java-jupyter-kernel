import py4j.GatewayServer;

public class Py4JExample {
    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new Py4JExample());

        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }

    public int test(int a){
        return a * 2;
    }
}
