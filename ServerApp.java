import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerApp {
  public static void main(String[] args) {
    String hostname = (args.length < 1) ? "127.0.0.1" : args[0];
    // int port = (args.length < 2) ? 6006 : args[1];
    int port = 6006;

    try {
      System.setProperty("java.rmi.server.hostname", hostname);

      ServerImpl server = new ServerImpl();
      Server stub = (Server) UnicastRemoteObject.exportObject(server, port);

      Registry registry = LocateRegistry.getRegistry();
      registry.bind("Server", stub);

      System.out.println("Server online");

    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
    }
  }
}
