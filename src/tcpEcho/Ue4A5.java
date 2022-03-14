package tcpEcho;
import java.io.*;
import java.net.*;

public class Ue4A5 {
	public final static int SERVERPORT = 4711;

	public static void main(String[] args) {
		Socket clientSocket = new Socket();
		try
		{
			clientSocket.setSoTimeout(50);
			try
			{
				clientSocket.connect(new InetSocketAddress("localhost", SERVERPORT), 1000);
				System.out.println("--- Connected to "+clientSocket.getInetAddress().getHostAddress()+" at port "+clientSocket.getPort()
				+" with local port "+clientSocket.getLocalPort());
				System.out.println("--- Enter text (end with empty line)");
				BufferedReader keys = new BufferedReader(new InputStreamReader(System.in));
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				while (true)
				{
					if (in.ready())
					{
						String line;
						if ((line=in.readLine())!=null)
							System.out.println("--- Received: "+line);
					}
					if (keys.ready())
					{
						String line;
						if ((line=keys.readLine())==null || "".equals(line)) break;
						System.out.println("--- Sending: "+line);
						out.write(line);
						out.newLine();
						out.flush();
					}
				}
			}
			catch (SocketTimeoutException e) { System.err.println("timed out"); }
		}
		catch (IOException e)
		{
			System.out.println("I/O error");
		}
		if (clientSocket != null)
		{
			System.out.println("--- Closing socket");
			try { clientSocket.close(); }
			catch (IOException e) { }
		}
		System.out.println("--- Program will stop");
	}

}
