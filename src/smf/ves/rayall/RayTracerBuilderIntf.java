package smf.ves.rayall;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RayTracerBuilderIntf extends Serializable,Remote {
  public RayTracerIntf getNewRayTracer() throws RemoteException;
}
