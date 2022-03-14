package smf.ves.rayserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import smf.ves.rayall.RayTracer;
import smf.ves.rayall.RayTracerLocal;
import smf.ves.rayall.Scene;

public class RayTracerServerThread extends Thread {
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Scene s;
	private int w, h;
	private RayTracer rt;
	private int[] values;
	
	public RayTracerServerThread(Socket newClient) {
		client = newClient;
		try {
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			s = (Scene) in.readObject();
			w = in.readInt();
			h = in.readInt();
			out.writeBoolean(true);
			out.flush();
			values = new int [w];
			rt = new RayTracerLocal();
			rt.init(s, w, h);
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		int y;
		while (true)
		{
			try {
				y = in.readInt();
				if (y < 0) break;
				rt.renderLine(values, y);
				out.writeObject(values);
				out.flush();
				out.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		try {
		    out.write(-1);
		    client.close();
	    }
		catch (IOException e) { /* can't do anything now */
			System.err.println("I/O-error while closing " + e.getMessage());
	    }
	    System.out.println("--- Closed " + client.getInetAddress() + " at port " + client.getPort());
	}
}
