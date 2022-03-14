package smf.ves.rayclient;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import smf.ves.rayall.RayTracer;
import smf.ves.rayall.RayTracerBuilderIntf;
import smf.ves.rayall.RayTracerIntf;
import smf.ves.rayall.Scene;

public class RayTracerRemoteRMI extends RayTracer {
  private RayTracerIntf rt;
  private boolean renderable;
  
  public void init(Scene s, int w, int h) {
    try {
      rt=((RayTracerBuilderIntf)Naming.lookup("rmi://localhost/RayTracerBuilder"))
        .getNewRayTracer();
      rt.init(s, w, h);
    }
    catch (RemoteException e) {
      System.err.println("Remote-error while connecting to server");
      return;
    }
    catch (IOException e) {
      System.err.println("I/O-error while connecting to server");
      return;
    }
    catch (NotBoundException e) {
      System.err.println("Not-bound-error while connecting to server");
      return;
    }
    renderable=true;
  }
  
  public void renderLine(int[] dest, int y) {
    if (renderable) try {
      System.arraycopy(rt.renderLine(y), 0, dest, 0, dest.length);
    }
    catch (RemoteException e) {
      System.err.println("Remote-error while calculating");
      renderable=false;
      return;
    }
  }

  public void cleanup() {
    if (renderable) try {
      rt.cleanup();
      rt=null;
    }
    catch (RemoteException e) {
      System.err.println("Remote-error while clenaup");
      return;
    }
  }
}
