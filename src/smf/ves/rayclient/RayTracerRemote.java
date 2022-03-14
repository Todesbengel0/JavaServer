package smf.ves.rayclient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import smf.ves.rayall.RayTracer;
import smf.ves.rayall.Scene;

public class RayTracerRemote extends RayTracer {
	private int threadNr;
	public final static int SERVERPORT = 4711;
	private Socket server;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public RayTracerRemote(int nr) {
		threadNr = nr;
	}

	public void init(Scene s, int w, int h) {
		server = new Socket();
		try {
			server.connect(new InetSocketAddress("localhost", SERVERPORT));
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			out.writeObject(s);
			out.writeInt(w);
			out.writeInt(h);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			if (!in.readBoolean())
			{
				System.err.println("Server did not acknowledge");
				return;
			}
			System.out.println("Connected thread " +threadNr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void renderLine(int[] dest, int y) {
		try {
			out.writeInt(y);
		out.flush();
		int[] values = (int[]) in.readObject();
		System.arraycopy(values, 0, dest, 0, dest.length);
		} catch (IOException e) {
			System.err.println("I/O-error while rendering");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Invalid server response");
			e.printStackTrace();
		}
	}

	public void cleanup() {
		try {
			out.writeInt(-1);
			out.flush();
			in.read();
			server.close();
			System.out.println("Closed connection for thread "+threadNr+" regularly");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
