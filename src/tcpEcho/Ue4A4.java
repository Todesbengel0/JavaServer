package tcpEcho;

import java.io.*;
import java.net.*;

public class Ue4A4 extends Thread {
	public final static int PORT = 4711;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket (PORT);
			serverSocket.setSoTimeout(1000);
			System.out.println("--- Started threaded TCP server on port "
					+serverSocket.getLocalPort()+", waiting (end with new line)");
			int cnt = 0;
			while (System.in.available()==0)
			{
				try
				{
					Socket client = serverSocket.accept();
					System.out.println("--- Got connection from "+client.getInetAddress()
					+" at port "+client.getPort()+", starting new Thread");
					(new Ue4A4(cnt++, client)).start();
				}
				catch (SocketTimeoutException e) { /* check again */ }
			}
			while (System.in.available()>0) System.in.read();
		}
		catch (IOException e)
		{
			System.err.println("there was an exception in main: "+e.getMessage());
		}
		if (serverSocket != null)
		{
			try
			{
				serverSocket.close();
				System.out.println("--- Server stopped");
			}
			catch (IOException e) {}
		}
	}
	
	private int threadNumber;
	private Socket localClient;
	private Ue4A4(int number, Socket client)
	{
		threadNumber = number;
		localClient = client;
	}
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(localClient.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localClient.getOutputStream()));
			System.out.println("--- Started thread "+threadNumber);
			out.write("Welcome to EchoServerThread in thread "+threadNumber);
			out.newLine();
			out.flush();
			String line;
			while ((line=in.readLine())!=null)
			{
				System.out.println("--- "+threadNumber+" Received: "+line);
				out.write(line);
				out.newLine();
				out.flush();
			}
			System.out.println("--- "+threadNumber+" Client shutdown");
			localClient.close();
		}
		catch (IOException e)
		{
			System.err.println("server thread "+threadNumber+": "+e.getMessage());
		}
	}

}
