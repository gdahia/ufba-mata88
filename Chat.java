import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface Chat extends Remote {
  public void sendMessage(Message message) throws RemoteException;
  public Vector<Message> getMessages() throws RemoteException;
}
