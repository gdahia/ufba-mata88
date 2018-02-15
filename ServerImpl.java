import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
  private String hostname;
  private HashMap<String, String> credentials;
  private HashMap<String, Session> sessions;
  private ArrayList<Server> replicas;
  private HashMap<String, Integer> visionRequests;
  private HashMap<String, Integer> visionMessages;
  private Integer clock;

  public ServerImpl() throws RemoteException {
    hostname = System.getProperty("java.rmi.server.hostname");
    credentials = new HashMap<String, String>();
    sessions = new HashMap<String, Session>();
    replicas = new ArrayList<Server>();
    visionRequests = new HashMap<String, Integer>();
    visionMessages = new HashMap<String, Integer>();
    clock = 0;
  }

  public String getHostname() {
    return hostname;
  }

  public ServerImpl(Server mainServer) throws RemoteException {
    this();

    try {
      mainServer.connectNewReplica(this);
    } catch (Exception e) {
      System.err.println("ServerImpl, ServerImpl exception: " + e.toString());
    }
  }

  public synchronized int registerVisionRequest(String claimant, int timestamp) {
    // register request
    visionRequests.put(claimant, timestamp);

    // register message timestamp
    visionMessages.put(claimant, timestamp);

    // update replicas clock
    if (timestamp >= clock)
      clock = timestamp + 1;

    return clock;
  }

  public void requestVision() {
    int timestamp = clock;

    // send request message to replicas
    for (Server replica : replicas) {
      try {
        int ack = replica.registerVisionRequest(hostname, timestamp);

        // register ack timestamp
        visionMessages.put(replica.getHostname(), ack);

        // update replicas clock
        synchronized (clock) {
          if (ack >= clock)
            clock = ack + 1;
          else
            clock++;
        }

      } catch (Exception e) {
        System.err.println("ServerImpl, requestVision: " + e.toString());
      }
    }

    // send request to itself
    registerVisionRequest(hostname, timestamp);
  }

  public synchronized void registerVisionRelease(String claimant, int timestamp) {
    // remove claimants requests
    visionRequests.remove(claimant);

    // register message timestamp
    visionMessages.put(claimant, timestamp);

    // update replicas clock
    if (timestamp >= clock)
      clock = timestamp + 1;
  }

  public void releaseVision() {
    int timestamp = clock;

    // remove own request messages
    registerVisionRelease(hostname, timestamp);

    // update replicas clock
    synchronized (clock) {
      clock++;
    }

    // send release message to replicas
    for (Server replica : replicas) {
      try {
        replica.registerVisionRelease(hostname, timestamp);

        // update replicas clock
        synchronized (clock) {
          clock++;
        }
      } catch (Exception e) {
        System.err.println("ServerImpl, releaseVision: " + e.toString());
      }
    }
  }

  public boolean visionGranted() {
    int timestamp = visionRequests.get(hostname);

    // check condition (i)
    for (Map.Entry<String, Integer> entry : visionRequests.entrySet()) {
      int replicaTimestamp = entry.getValue();
      String replicaHostname = entry.getKey();

      if (replicaTimestamp < timestamp
          || (replicaTimestamp == timestamp && replicaHostname.compareTo(hostname) < 0))
        return false;
    }

    // check condition (ii)
    for (Map.Entry<String, Integer> entry : visionMessages.entrySet()) {
      int replicaTimestamp = entry.getValue();
      String replicaHostname = entry.getKey();

      if (replicaTimestamp <= timestamp && replicaHostname != hostname)
        return false;
    }

    return true;
  }

  public void connectNewReplica(Server newReplica) {
    // wait until access to vision is granted
    requestVision();
    while (!visionGranted()) {
    }

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

    releaseVision();
  }

  public void addReplica(Server server) {
    try {
      String serverHostname = server.getHostname();
      replicas.add(server);
      System.out.println("Connected to server at " + serverHostname);
    } catch (Exception e) {
      System.err.println("ServerImpl, addReplica exception: " + e.toString());
    }
  }

  public Session getSession(String username, String userCredentials) {
    // wait until access to vision is granted
    requestVision();
    while (!visionGranted()) {
    }

    // get corresponding stored user credentials
    String creds = credentials.get(username);

    // check if given credentials match stored ones
    if (creds != null && creds.equals(userCredentials)) {
      System.out.println("User \"" + username + "\" logged in");

      releaseVision();

      return sessions.get(username);
    } else {
      releaseVision();

      return null;
    }
  }

  public synchronized void replicateUser(
      String username, String userCredentials, Session userSession) throws RemoteException {
    credentials.put(username, userCredentials);
    sessions.put(username, userSession);
    System.out.println("User \"" + username + "\" registered");
  }

  public boolean addUser(String username, String userCredentials) throws RemoteException {
    // wait until access to vision is granted
    requestVision();
    while (!visionGranted()) {
    }

    // check if user already is added to not add repeated users
    if (credentials.get(username) != null) {
      releaseVision();
      return false;
    } else {
      // create user locally
      Session session = new SessionImpl(username, this);
      credentials.put(username, userCredentials);
      sessions.put(username, session);
      System.out.println("User \"" + username + "\" registered");

      // communicate user creation to replicas
      for (Server replica : replicas) {
        try {
          replica.replicateUser(username, userCredentials, session);
        } catch (Exception e) {
          System.err.println("ServerImpl, addUser exception: " + e.toString());
        }
      }

      releaseVision();

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
    // wait until vision is granted
    requestVision();
    while (!visionGranted()) {
    }

    // attempt to get user session corresponding to given username
    Session sess = sessions.get(username);

    if (sess == null) {
      // do not add nonexistent users to chats
      releaseVision();
      return false;
    }

    try {
      // add chat to user session
      sess.addChat(chat);
      releaseVision();
      return true;
    } catch (Exception e) {
      System.err.println("ServerImpl, addUserToChat exception: " + e.toString());
      releaseVision();
      return false;
    }
  }

  public synchronized void dereplicateUser(String username) throws RemoteException {
    sessions.remove(username);
    credentials.remove(username);
    System.out.println("User \"" + username + "\" was deleted");
  }

  public void removeUser(String username) {
    // wait until vision is granted
    requestVision();
    while (!visionGranted()) {
    }

    // remove user locally
    sessions.remove(username);
    credentials.remove(username);
    System.out.println("User \"" + username + "\" was deleted");

    // comunicate user removal to replicas
    for (Server replica : replicas) {
      try {
        replica.dereplicateUser(username);
      } catch (Exception e) {
        System.err.println("ServerImpl, removeUser exception: " + e.toString());
      }
    }

    releaseVision();
  }
}
