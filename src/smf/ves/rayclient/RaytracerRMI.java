package smf.ves.rayclient;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

import smf.ves.rayall.RayTracer;
import smf.ves.rayall.RayTracerIntf;
import smf.ves.rayall.RayTracerLocal;
import smf.ves.rayall.Scene;

public class RayTracerRMI extends UnicastRemoteObject implements RayTracerIntf {
  private static final long serialVersionUID = 1L;
  private RayTracer rt;
  private int[] values;
  
  public RayTracerRMI() throws RemoteException {
    super();
    rt=new RayTracerLocal();
  }
  
  public void init(Scene s, int w, int h) throws RemoteException {
    values=new int[w];
    rt.init(s, w, h);
    try {
      System.out.println("initialized RayTracerRMI for "+getClientHost());
    }
    catch (ServerNotActiveException e) {
      System.out.println("local init call in RayTracerRMI?");
    }
  }
  
  public int[] renderLine(int y) throws RemoteException {
    rt.renderLine(values, y);
    return values;
  }
  
  public void cleanup() throws RemoteException {
    rt.cleanup();
    rt=null;
    try {
      System.out.println("cleaned up for "+getClientHost());
    }
    catch (ServerNotActiveException e) {
      System.out.println("local cleanup call in RayTracerRMI?");
    }
  }
}
