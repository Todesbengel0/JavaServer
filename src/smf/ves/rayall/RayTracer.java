package smf.ves.rayall;

/**
 * This is the general layout of a raytracer
 */
public abstract class RayTracer {
  public abstract void init(Scene s, int w, int h);
  public abstract void renderLine(int[] dest, int y);
  public abstract void cleanup();
}
