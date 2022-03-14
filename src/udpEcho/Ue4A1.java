package udpEcho;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Ue4A1 {
	public final static int PORT = 4711;

	public static void main(String[] args) {
		try
		{
			DatagramSocket serverSocket = new DatagramSocket(PORT);
			DatagramPacket packet = new DatagramPacket(new byte[512], 512);
			System.out.println("--- Started UDP server on port "
			+serverSocket.getLocalPort());
			while (true)
			{
				packet.setLength(512);
				serverSocket.receive(packet);
				byte[] data = packet.getData();
				String line = new String(data, 0, packet.getLength());
				if (packet.getLength() == 0 || line.equals("---shutdown"))
					break;
				System.out.println("--- Received: "+line);
				packet.setAddress(packet.getAddress());
				packet.setPort(packet.getPort());
				serverSocket.send(packet);
			}
			serverSocket.close();
			System.out.println("--- Server stopped");
		}
		catch (IOException e)
		{
			System.err.println("there was an exception in main: "+e.getMessage());
		}

	}

}
