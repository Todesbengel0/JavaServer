package udpEcho;
import java.io.*;
//import java.util.*;
import java.net.*;

public class ue4A2 {
	public final static int SERVERPORT = 4711;

	public static void main(String[] args) {
		/*
		Scanner scn = new Scanner(System.in);
		
		System.out.print("Enter string : ");
		String str = scn.nextLine();
		System.out.println("You entered : " + str);
		scn.close();
		*/
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line;
		System.out.println("Please enter lines of text (end with empty line)");
		try
		{
			DatagramSocket clientSocket = new DatagramSocket();
			byte[] dataBuffer;
			DatagramPacket packet = new DatagramPacket(dataBuffer = new byte[512], 512);
			clientSocket.setSoTimeout(50);
			while (true)
			{
				try
				{
					packet.setData(dataBuffer);
					packet.setLength(dataBuffer.length);
					clientSocket.receive(packet);
					System.out.println("--- Received: " +
							new String(packet.getData(), 0, packet.getLength()));
				}
				catch (SocketTimeoutException e) { /* no packet */ }
				if(in.ready())
				{
					packet.setAddress(InetAddress.getByName("localhost"));
					packet.setPort(SERVERPORT);
					if((line = in.readLine()) == null || "".equals(line))
					{
						byte[] data = "---shutdown".getBytes();
						packet.setData(data);
						packet.setLength(data.length);
						clientSocket.send(packet);
						break;
					}
					System.out.println("--- Sending: "+line);
					packet.setData(line.getBytes());
					packet.setLength(line.length());
					clientSocket.send(packet);
				}
			}
			clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("I/O error");
		}
		System.out.println("Program will stop");
	}

}
