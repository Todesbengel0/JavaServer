package httpClient;
import java.io.*;
import java.net.*;

public class Ue7A1 {
	public final static String SERVERNAME = "www.tombleek.de";
	public final static int SERVERPORT = 80;

	public static void main(String[] args) {
		Socket socket = null;
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress(SERVERNAME, SERVERPORT), 1000);
			System.out.println("--- Connected to "+socket.getInetAddress().getHostAddress()+" at port "+socket.getPort()
								+" with local port "+socket.getLocalPort());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			out.print("GET / HTTP/1.1\r\n");
			out.print("Host: "+SERVERNAME+"\r\n");
			out.print("\r\n");
			out.flush();
			socket.shutdownOutput();
			System.out.println("--- Query sent, reply:");
			
			String line;
			while ((line=in.readLine())!=null)
				System.out.println(line);
		}
		catch (IOException e)
		{
			System.out.println("I/O error");
		}
		if (socket != null)
		{
			System.out.println("--- Closing socket");
			try { socket.close(); }
			catch (IOException e) { }
		}
		System.out.println("--- Program will stop");
	}

}
