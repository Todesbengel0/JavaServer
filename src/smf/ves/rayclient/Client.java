package smf.ves.rayclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import smf.ves.rayall.RayTracer;
import smf.ves.rayall.RayTracerLocal;
import smf.ves.rayall.Scene;

public class Client extends JFrame {
  private static final long serialVersionUID = 1L;
  
  public final static int CALCMODE_LINE = 1;
  public final static int CALCMODE_BLOCK = 2;
  
  public final static int MACHMODE_LOCAL = 1;
  public final static int MACHMODE_REMOTE = 2;
  
  public final static int MULT = 30, THREADS = 10;
  public final static int CALCMODE = CALCMODE_BLOCK;
  
  public final static int MACHMODE = MACHMODE_LOCAL; //local or remote?
  
  private final static int XRES = 64 * MULT, YRES = 48 * MULT;
  
  /** the image array containing the rendered picture */
  private int image[][];
  /** the buffered image instance for drawing the rendered picture */
  private BufferedImage screen;
  /** the panel containing the buffered image */
  private JPanel drawPanel;

  public Client() {
    super("RayTracer");
    screen = new BufferedImage(XRES, YRES, BufferedImage.TYPE_INT_RGB);
    drawPanel = new JPanel() {
      private static final long serialVersionUID = 1L;

      public void update(Graphics g) {
        paint(g);
      }

      public void paint(Graphics g) {
        synchronized (screen) {
          g.drawImage(screen, 0, 0, null);
        }
      }
    };
    drawPanel.setSize(XRES, YRES);
    drawPanel.setPreferredSize(new Dimension(XRES, YRES));
    add(new JScrollPane(drawPanel));
    pack();
    setSize(800, 600);
    Graphics gc = screen.getGraphics();
    gc.setColor(getBackground());
    gc.fillRect(0, 0, XRES, YRES);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setVisible(true);
  }
  private class CalcThread extends Thread {
    private RayTracer rt;
    private int start, stop, step;

    public CalcThread(RayTracer rt, int start, int stop, int step) {
      this.rt = rt;
      this.start = Math.max(start, 0);
      this.stop = Math.min(stop, YRES);
      this.step = step;
    }

    public void run() {
      for (int y = start; y < stop; y += step) {
        int[] line;
        rt.renderLine(line = image[y], y);
        synchronized (screen) {
          screen.setRGB(0, y, XRES, 1, line, 0, XRES);
        }
        drawPanel.repaint();
      }
      rt.cleanup();
    }
  }

  public static void main(String[] args) {
    Client me = new Client();
    me.image = new int[YRES][XRES];
    Scene s = SceneToCalc.setup();
    for (int i = 0; i < THREADS; i++) {
      RayTracer rt = null;
      switch (MACHMODE) {
        case MACHMODE_LOCAL: // calc on local machine
          rt = new RayTracerLocal();
          break;
        case MACHMODE_REMOTE: // calc on remote machine
          rt = new RayTracerRemote(i);
          break;
      }
      rt.init(s, XRES, YRES);
      switch (CALCMODE) {
        case CALCMODE_LINE: // calc line-wise
          (me.new CalcThread(rt, i, YRES, THREADS)).start();
          break;
        case CALCMODE_BLOCK: // calc block-wise
          (me.new CalcThread(rt, i * YRES / THREADS, i * YRES / THREADS + YRES
              / THREADS, 1)).start();
          break;
      }
    }
  }
}
