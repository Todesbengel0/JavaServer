package smf.ves.rayall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RayTracerIntf extends Remote {
  public void init(Scene s, int w, int h) throws RemoteException;
  public int[] renderLine(int y) throws RemoteException;
  public void cleanup() throws RemoteException;
}
