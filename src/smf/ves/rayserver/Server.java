package smf.ves.rayserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
  public static void main(String[] args) {
    //--- RMI server
    
    try {
      ServerRMI s=new ServerRMI();
      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
      Naming.rebind("rmi://localhost/RayTracerBuilder", s);
    }
    catch (RemoteException e) {
      System.err.println("Remote-error while building server");
      e.printStackTrace();
      return;
    }
    catch (IOException e) {
      System.err.println("IO-error while building server");
      return;
    }
    
    //--- socket server
    int port = 4711;
    if (args != null && args.length == 1 && args[0] != null) {
      try {
        port = Integer.parseInt(args[0]);
      }
      catch (NumberFormatException e) {
        System.err.println("invalid port number");
        System.exit(1);
      }
    }
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(1000);
      System.out.println("--- Started threaded server on port "
          + serverSocket.getLocalPort() + ", waiting (end with new line)");
      while (System.in.available() == 0) {
        try {
          Socket client = serverSocket.accept();
          System.out.println("--- Got connection from "
              + client.getInetAddress() + " at port " + client.getPort());
          new RayTracerServerThread(client);
        }
        catch (SocketTimeoutException e) {
          /* not really a problem, just to check the input stream again */
        }
      }
      while (System.in.available() > 0) {
        System.in.read(); // clean up
      }
    }
    catch (IOException e) {
      System.err.println("there was an exception in main: " + e.getMessage());
    }
    if (serverSocket != null) {
      try {
        // close the server socket
        serverSocket.close();
        System.out.println("--- Server stopped");
      }
      catch (IOException e) { /* can't do anything now */
      }
    }
    System.exit(0);
  }
}
