import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientApp {
  public static void main(String[] args) {
    String host = (args.length < 1) ? null : args[0];
    try {
      Registry registry = LocateRegistry.getRegistry(host);
      Server server = (Server) registry.lookup("Server");
      Client client = new Client(server);
    } catch (Exception e) {
      System.err.println("ClientApp exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
