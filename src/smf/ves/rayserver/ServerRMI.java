package smf.ves.rayserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import smf.ves.rayall.RayTracerBuilderIntf;
import smf.ves.rayall.RayTracerIntf;
import smf.ves.rayclient.RayTracerRMI;

public class ServerRMI extends UnicastRemoteObject implements RayTracerBuilderIntf {
  private static final long serialVersionUID = 1L;

  public ServerRMI() throws RemoteException {
  }
  
  public RayTracerIntf getNewRayTracer() throws RemoteException {
    return new RayTracerRMI();
  }
}