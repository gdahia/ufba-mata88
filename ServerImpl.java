import java.util.HashMap;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
  private HashMap<String, String> credentials;
  private HashMap<String, Session> sessions;
  private ArrayList<Server> replicas;

  public ServerImpl() throws RemoteException {
    credentials = new HashMap<String, String>();
    sessions = new HashMap<String, Session>();
    replicas = new ArrayList<Server>();
  }

  public ServerImpl(Server mainServer) throws RemoteException {
    this();

    try {
      mainServer.connectNewReplica(this);
    } catch (Exception e) {
      System.err.println("ServerImpl, ServerImpl exception: " + e.toString());
    }
  }

  public synchronized void connectNewReplica(Server newReplica) {
    // connect to every other replica
    for (Server replica : replicas) {
      try {
        newReplica.addReplica(replica);
        replica.addReplica(newReplica);
      } catch (Exception e) {
        System.err.println("ServerImpl, connectNewReplica exception (1): " + e.toString());
      }
    }

    // connect itself
    try {
      this.addReplica(newReplica);
      newReplica.addReplica(this);
    } catch (Exception e) {
      System.err.println("ServerImpl, connectNewReplica exception (2): " + e.toString());
    }
  }

  public synchronized void addReplica(Server server) {
    replicas.add(server);
  }

  public synchronized Session getSession(String username, String userCredentials) {
    // get corresponding stored user credentials
    String creds = credentials.get(username);

    // check if given credentials match stored ones
    if (creds != null && creds.equals(userCredentials)) {
      System.out.println("User \"" + username + "\" logged in");
      return sessions.get(username);
    } else
      return null;
  }

  public synchronized boolean addUser(String username, String userCredentials)
      throws RemoteException {
    if (credentials.get(username) != null)
      // do not add repeated users
      return false;
    else {
      System.out.println("User \"" + username + "\" registered");

      // store given credentials
      credentials.put(username, userCredentials);

      // create user session
      sessions.put(username, new SessionImpl(username, this));

      return true;
    }
  }

  public synchronized void addChat(Session sess) throws RemoteException {
    // get chat creator username
    String username = sess.getUsername();

    // create new chat
    Chat chat = new ChatImpl(this, username);

    try {
      // add chat to creator session
      sess.addChat(chat);
      System.out.println("User \"" + username + "\" created a new chat");
    } catch (Exception e) {
      System.err.println("ServerImpl, addChat exception: " + e.toString());
    }
  }

  public synchronized boolean addUserToChat(String username, Chat chat) {
    // attempt to get user session corresponding to given username
    Session sess = sessions.get(username);

    if (sess == null)
      // do not add nonexistent users to chats
      return false;
    try {
      // add chat to user session
      sess.addChat(chat);

      return true;
    } catch (Exception e) {
      System.err.println("ServerImpl, addUserToChat exception: " + e.toString());
      return false;
    }
  }

  public synchronized void removeUser(String username) {
    sessions.remove(username);
    credentials.remove(username);
    System.out.println("User \"" + username + "\" was deleted");
  }
}
