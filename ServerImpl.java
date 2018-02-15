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
  private HashMap<String, Integer> replicasRequests;
  private HashMap<String, Integer> replicasMessages;
  private Integer replicasClock;

  public ServerImpl() throws RemoteException {
    hostname = System.getProperty("java.rmi.server.hostname");
    credentials = new HashMap<String, String>();
    sessions = new HashMap<String, Session>();
    replicas = new ArrayList<Server>();
    replicasRequests = new HashMap<String, Integer>();
    replicasMessages = new HashMap<String, Integer>();
    replicasClock = 0;
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

  public synchronized int registerReplicasRequest(String claimant, int timestamp) {
    // register request
    replicasRequests.put(claimant, timestamp);

    // register message timestamp
    replicasMessages.put(claimant, timestamp);

    // update replicas clock
    if (timestamp >= replicasClock)
      replicasClock = timestamp + 1;

    return replicasClock;
  }

  public void requestReplicas() {
    int timestamp = replicasClock;

    // send request message to replicas
    for (Server replica : replicas) {
      try {
        int ack = replica.registerReplicasRequest(hostname, timestamp);

        // register ack timestamp
        replicasMessages.put(replica.getHostname(), ack);

        // update replicas clock
        synchronized (replicasClock) {
          if (ack >= replicasClock)
            replicasClock = timestamp + 1;
          else
            replicasClock++;
        }

      } catch (Exception e) {
        System.err.println("ServerImpl, requestReplicas: " + e.toString());
      }
    }

    // send request to itself
    registerReplicasRequest(hostname, timestamp);
  }

  public synchronized void registerReplicasRelease(String claimant, int timestamp) {
    // remove claimants requests
    replicasRequests.remove(claimant);

    // register message timestamp
    replicasMessages.put(claimant, timestamp);

    // update replicas clock
    if (timestamp >= replicasClock)
      replicasClock = timestamp + 1;
  }

  public void releaseReplicas() {
    int timestamp = replicasClock;

    // remove own request messages
    registerReplicasRelease(hostname, timestamp);

    // update replicas clock
    synchronized (replicasClock) {
      replicasClock++;
    }

    // send release message to replicas
    for (Server replica : replicas) {
      try {
        replica.registerReplicasRelease(hostname, timestamp);

        // update replicas clock
        synchronized (replicasClock) {
          replicasClock++;
        }
      } catch (Exception e) {
        System.err.println("ServerImpl, releaseReplicas: " + e.toString());
      }
    }
  }

  public boolean replicasGranted() {
    int timestamp = replicasRequests.get(hostname);

    // check condition (i)
    for (Map.Entry<String, Integer> entry : replicasRequests.entrySet()) {
      int replicaTimestamp = entry.getValue();
      String replicaHostname = entry.getKey();

      if (replicaTimestamp < timestamp
          || (replicaTimestamp == timestamp && replicaHostname.compareTo(hostname) < 0))
        return false;
    }

    // check condition (ii)
    for (Map.Entry<String, Integer> entry : replicasMessages.entrySet()) {
      int replicaTimestamp = entry.getValue();
      String replicaHostname = entry.getKey();

      if (replicaTimestamp <= timestamp && replicaHostname != hostname)
        return false;
    }

    return true;
  }

  public void connectNewReplica(Server newReplica) {
    // wait until access to replicas is granted
    requestReplicas();
    while (!replicasGranted())
      ;

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

    releaseReplicas();
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
