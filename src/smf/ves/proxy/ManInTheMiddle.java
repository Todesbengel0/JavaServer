package smf.ves.proxy;

import java.io.*;
import java.net.*;

public class ManInTheMiddle extends Thread
{
	public final static int MYPORT = 4711;
	public final static String SVADDR = "localhost";
	public final static int SVPORT = 4712;

	public static void main(String[] args)
	{
		ServerSocket server = null;
		try
		{
			server = new ServerSocket(MYPORT);
			server.setSoTimeout(1000);
			System.out.println("--- Started threaded server on port "+server.getLocalPort()+", waiting (end with new line)");
			int cnt = 0;
			while (System.in.available() == 0)
			{
				try
				{
					Socket client = server.accept();
					System.out.println("--- Got connection from "+client.getInetAddress()+" at port "+client.getPort()+", starting new Thread");
					Socket clientServer = new Socket(SVADDR, SVPORT);
					(new ManInTheMiddle(cnt++, client, clientServer)).start();
				}
				catch (SocketTimeoutException e) { }
			}
			while (System.in.available() > 0) System.in.read();
		}
		catch (IOException e)
		{
			System.err.println("there was an exception in main: "+e.getMessage());
		}
		if (server!=null)
		{
			try
			{
				server.close();
				System.out.println("--- Server stopped");
			}
			catch (IOException e) { }
		}
	}
	
	private int threadNumber;
	private Socket socket1, socket2;
	private ManInTheMiddle(int number, Socket s1, Socket s2)
	{
		threadNumber = number;
		socket1 = s1;
		socket2 = s2;
	}
	
	public void run()
	{
		try
		{
			socket1.setSoTimeout(10);
			socket2.setSoTimeout(10);
			InputStream in1 = socket1.getInputStream(), in2 = socket2.getInputStream();
			OutputStream out1 = socket1.getOutputStream();
			OutputStream out2 = socket2.getOutputStream();
			
			while (true)
			{
				int v;
				try
				{
					if ((v = in1.read()) == -1) break;
					out2.write(v);
					System.out.print(">>> ");
					printChar(v);
					while (in1.available() > 0)
					{
						out2.write(v = in1.read());
						printChar(v);
					}
					out2.flush();
					System.out.println();
					System.out.println();
				}
				catch (SocketTimeoutException e) {}
				try
				{
					if ((v = in2.read()) == -1) break;
					out1.write(v);
					System.out.print("<<< ");
					printChar(v);
					while (in2.available() > 0)
					{
						out1.write(v = in2.read());
						printChar(v);
					}
					out1.flush();
					System.out.println();
					System.out.println();
				}
				catch (SocketTimeoutException e) {}
				System.out.println("--- "+threadNumber+" shutdown");
				socket1.close();
				socket2.close();
			}
		}
		catch (IOException e)
		{
			System.err.println("there was an exception at the thread "+threadNumber
			+": "+e.getMessage());
		}
	}
	
	private static void printChar(int v)
	{
		System.out.print(v>=32 && v<=122 ? (char)v : '.');
	}

}
