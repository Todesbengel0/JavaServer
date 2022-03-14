package smf.ves.rayclient;

import smf.ves.rayall.Light;
import smf.ves.rayall.LightObjList;
import smf.ves.rayall.RGBfCol;
import smf.ves.rayall.RenderableObjList;
import smf.ves.rayall.Scene;
import smf.ves.rayall.Sphere;
import smf.ves.rayall.Surface;
import smf.ves.rayall.Vector3f;

/**
 * The scene class encapsulates camera and world data.
 */
public class SceneToCalc {
  /**
   * Scene constructor reads all camera and world details from a file.
   */
  public static Scene setup() {
    Scene scene = new Scene();
    scene.objects = new RenderableObjList();
    scene.lights = new LightObjList();
    setUpEye(scene);
    setUpEnvironment(scene);
    createThings(scene);
    createLights(scene);
    return scene;
  }

  private static void setUpEye(Scene scene) {
    scene.eye = new Vector3f(0.0f, -8.0f, 3.0f);
    scene.up = new Vector3f(0.0f, 0.0f, 1.0f);
    scene.lookat = new Vector3f(0.0f, -2.0f, 0.0f);
    scene.fov = 75.0f;
  }

  private static void setUpEnvironment(Scene scene) {
    Surface s;
    scene.background = RGBfCol.create(0.078f, 0.361f, 0.753f);
    s = new Surface(0.1f, 0.6f, 0.3f, 0.15f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
    scene.objects.addElement(new Sphere(s, new Vector3f(0.0f, 0.0f, -2000.0f),
        2000.0f));
  }

  private static void createGroup(Scene scene, Surface cS, float x, float y,
      boolean upper) {
    scene.objects.addElement(new Sphere(cS, new Vector3f(x + 0.0f, y + 0.0f,
        0.5f), 0.5f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x + 0.643951f,
        y + 0.172546f, 0.5f), 0.166667f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x + 0.172546f,
        y + 0.643951f, 0.5f), 0.166667f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x - 0.471405f,
        y + 0.471405f, 0.5f), 0.166667f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x - 0.643951f,
        y - 0.172546f, 0.5f), 0.166667f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x - 0.172546f,
        y - 0.643951f, 0.5f), 0.166667f));
    scene.objects.addElement(new Sphere(cS, new Vector3f(x + 0.471405f,
        y - 0.471405f, 0.5f), 0.166667f));
    if (upper) {
      scene.objects.addElement(new Sphere(cS, new Vector3f(x + 0.272166f,
          y + 0.272166f, 1.044331f), 0.166667f));
      scene.objects.addElement(new Sphere(cS, new Vector3f(x - 0.371785f,
          y + 0.0996195f, 1.044331f), 0.166667f));
      scene.objects.addElement(new Sphere(cS, new Vector3f(x - 0.0996195f,
          y - 0.371785f, 1.044331f), 0.166667f));
    }
  }

  private static void createMultiGroup(Scene scene, Surface cS, float x, float y) {
    createGroup(scene, cS, x, y, true);
    createGroup(scene, cS, x - 2.0f, y + 2.0f, true);
    createGroup(scene, cS, x - 2.0f, y - 2.0f, true);
    createGroup(scene, cS, x + 2.0f, y - 2.0f, true);
    createGroup(scene, cS, x + 2.0f, y + 2.0f, true);
  }

  private static void createThings(Scene scene) {
    Surface s;
    s = new Surface(0.0f, 0.0f, 1.0f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f, 1.0f);
    scene.objects.addElement(new Sphere(s, new Vector3f(-1.5f, -1.5f, 0.5f),
        0.5f));
    s = new Surface(1.0f, 0.0f, 0.0f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f, 1.0f);
    scene.objects.addElement(new Sphere(s, new Vector3f(-1.0f, 0.0f, 0.5f),
        0.5f));
    s = new Surface(0.5f, 0.45f, 0.35f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f,
        1.0f);
    scene.objects
        .addElement(new Sphere(s, new Vector3f(0.0f, 1.0f, 0.5f), 0.5f));
    s = new Surface(1.0f, 1.0f, 0.0f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f, 1.0f);
    scene.objects
        .addElement(new Sphere(s, new Vector3f(1.0f, 0.0f, 0.5f), 0.5f));
    s = new Surface(0.0f, 1.0f, 1.0f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f, 1.0f);
    scene.objects.addElement(new Sphere(s, new Vector3f(1.5f, -1.5f, 0.5f),
        0.5f));
    s = new Surface(0.5f, 0.45f, 0.35f, 0.07f, 1.0f, 0.8f, 3.0f, 0.5f, 0.0f,
        1.0f);
    createGroup(scene, s, 4.5f, 2.0f, false);
    createGroup(scene, s, -4.5f, 2.0f, false);
    createGroup(scene, s, 6.0f, 0.5f, true);
    createGroup(scene, s, -6.0f, 0.5f, true);
    createGroup(scene, s, 4.5f, -1.0f, false);
    createGroup(scene, s, -4.5f, -1.0f, false);
    createGroup(scene, s, 3.0f, -2.5f, true);
    createGroup(scene, s, -3.0f, -2.5f, true);
    createGroup(scene, s, 1.5f, -4.0f, false);
    createGroup(scene, s, -1.5f, -4.0f, false);
    createGroup(scene, s, 0.0f, -5.5f, true);
    for (int i = 0; i < 3; i++) {
      createMultiGroup(scene, s, -7.0f, 7.0f + (float) i * 7.0f);
      createMultiGroup(scene, s, +0.0f, 6.0f + (float) i * 7.0f);
      createMultiGroup(scene, s, +7.0f, 7.0f + (float) i * 7.0f);
    }
  }

  private static void createLights(Scene scene) {
    scene.lights.addElement(new Light(Light.AMBIENT, null, 0.5f, 0.5f, 0.5f));
    scene.lights.addElement(new Light(Light.POINT, new Vector3f(10.0f, -3.0f,
        10.0f), 0.8f, 0.8f, 0.8f));
    scene.lights.addElement(new Light(Light.POINT, new Vector3f(0.0f, 0.0f,
        2.0f), 0.3f, 0.3f, 0.3f));
  }
}
