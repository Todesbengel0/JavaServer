package smf.ves.rayall;

/**
 * This is the local implementation of a raytracer
 */
public class RayTracerLocal extends RayTracer {
  /** horizontal direction of screen in worldspace */
  private Vector3f Du = new Vector3f();
  /** vertical direction of screen in worldspace */
  private Vector3f Dv = new Vector3f();
  /** vector from eye to topleft corner of screen in worldspace */
  private Vector3f Vp = new Vector3f();
  /** the world description, with objects, lights, and camera info */
  private Scene scene;
  /** the screen size */
  private int width, height;
  /** temporary variables of this RayTracer instance */
  private Vector3f dir = new Vector3f();
  private Vector3f t1 = new Vector3f(), t2 = new Vector3f(),
      t3 = new Vector3f();
  private Ray ray = new Ray();

  /**
   * Initializes an instance of the raytracer with the window size and the world
   * description.
   * 
   * @param Scene
   *          world description
   * @param w
   *          width of image
   * @param h
   *          height of image
   */
  public void init(Scene s, int w, int h) {
    float fl;
    Vector3f look, crossp;
    scene = s;
    width = w;
    height = h;
    // Compute viewing matrix that maps a
    // screen coordinate to a ray direction
    look = new Vector3f(scene.lookat.x - scene.eye.x, scene.lookat.y
        - scene.eye.y, scene.lookat.z - scene.eye.z);
    crossp = new Vector3f();
    crossp.cross(look, scene.up);
    Du.normalize(crossp);
    crossp.cross(look, Du);
    Dv.normalize(crossp);
    // fl = (width / (2.0f*FPU.tan((0.5f*scene.fov)*FPU.PI/180.0f)));
    fl = ((float) width)
        / (2.0f * (float) Math.tan((double) ((0.5f * scene.fov)
            * (float) Math.PI / 180.0f)));
    // fl=(0.5f*scene.fov)*FloatUtil.PI()/180.0f;
    // fl=((float)width) / (2.0f*FPU.sin(fl)/FPU.cos(fl));
    Vp.normalize(look);
    Vp.x = Vp.x * fl - 0.5f * ((float) width * Du.x + (float) height * Dv.x);
    Vp.y = Vp.y * fl - 0.5f * ((float) width * Du.y + (float) height * Dv.y);
    Vp.z = Vp.z * fl - 0.5f * ((float) width * Du.z + (float) height * Dv.z);
    // prepare scene
    // Compute viewing matrix that maps a
    // screen coordinate to a ray direction
    look = new Vector3f(scene.lookat.x - scene.eye.x, scene.lookat.y
        - scene.eye.y, scene.lookat.z - scene.eye.z);
    crossp = new Vector3f();
    crossp.cross(look, scene.up);
    Du.normalize(crossp);
    crossp.cross(look, Du);
    Dv.normalize(crossp);
    fl = (float) width
        / (2.0f * (float) Math.tan((double) ((0.5f * scene.fov)
            * (float) Math.PI / 180.0f)));
    Vp.normalize(look);
    Vp.x = Vp.x * fl - 0.5f * ((float) width * Du.x + (float) height * Dv.x);
    Vp.y = Vp.y * fl - 0.5f * ((float) width * Du.y + (float) height * Dv.y);
    Vp.z = Vp.z * fl - 0.5f * ((float) width * Du.z + (float) height * Dv.z);
  }

  /**
   * Render a line of pixels by creating ray through the top-left corner of the
   * requested pixel, tracing the ray through the scene, and shading
   * accordingly.
   */
  public void renderLine(int[] dest, int y) {
    Renderable rayObject;
    int x;
    // calculate requested line
    for (x = 0; x < width; x++) {
      dir
          .init((float) x * Du.x + (float) y * Dv.x + Vp.x, (float) x * Du.y
              + (float) y * Dv.y + Vp.y, (float) x * Du.z + (float) y * Dv.z
              + Vp.z);
      ray.init(scene.eye, dir);
      rayObject = ray.trace(scene.objects);
      dest[x] = RGBfCol.toRGB(rayObject != null ? rayObject.Shade(ray, scene,
          t1, t2, t3) : scene.background);
    }
  }

  /**
   * Clean up
   */
  public void cleanup() {
    // nothing to do
  }
}
