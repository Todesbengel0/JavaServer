package smf.ves.rayall;

import java.io.Serializable;

/**
 * The scene class encapsulates camera and world data.
 */
public final class Scene implements Serializable {
  private static final long serialVersionUID = 1L;
  /** eye position */
  public Vector3f eye;
  /** direction of view */
  public Vector3f lookat;
  /** up vector from eye position */
  public Vector3f up;
  /** field of view */
  public float fov;
  /** list of objects in the world */
  public RenderableObjList objects;
  /** list of lights in the world */
  public LightObjList lights;
  /** the RGBfCol coded color of the scene background */
  public long background;
}
