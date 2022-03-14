package udpEcho;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

import javax.swing.*;

public class EchoClient extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextArea text;
	private JTextField inputField;
	private JButton sendButton;
	public final static int SERVERPORT = 4711;
	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private String userID;
	
	public static void main(String[] args) {
		final String server, user;
		if ((server=(String)JOptionPane.showInputDialog(null, "Please enter your chat server",
				"ChatClient", JOptionPane.QUESTION_MESSAGE, null, null, "localhost"))==null
				|| server.equals("")) return;
		if ((user=(String)JOptionPane.showInputDialog(null, "Please enter your user name",
				"ChatClient", JOptionPane.QUESTION_MESSAGE, null, null, "smf"))==null
				|| user.equals("")) return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DatagramSocket preparedSocket = null;
				DatagramPacket preparedPacket = new DatagramPacket(new byte[512], 512);
				try
				{
					preparedSocket = new DatagramSocket();
					preparedPacket.setAddress(InetAddress.getByName(server));
					preparedPacket.setPort(SERVERPORT);
				}
				catch (SocketException e)
				{
					JOptionPane.showMessageDialog(null, "Error creating socket");
					System.exit(-1);
				}
				catch (UnknownHostException e)
				{
					JOptionPane.showMessageDialog(null, "Could not resolve host "+server);
					System.exit(-1);
				}
				new EchoClient(server, user, preparedSocket, preparedPacket);
			}
		});
	}
	
	public EchoClient(String server, String user, DatagramSocket preparedSocket, DatagramPacket preparedPacket) {
		super("ChatClient ("+user+" at "+server+")");
		userID = user;
		socket = preparedSocket;
		sendPacket = preparedPacket;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(text = new JTextArea(), BorderLayout.CENTER);
		text.setPreferredSize(new Dimension(400, 300));
		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.add(inputField = new JTextField(), BorderLayout.CENTER);
		inputField.addActionListener(this); //?
		bottom.add(sendButton = new JButton("send"), BorderLayout.EAST);
		sendButton.addActionListener(this);
		add(bottom, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		inputField.requestFocus();
		(new EchoClientUpdater()).start();
	}
	
	private class EchoClientUpdater extends Thread
	{
		public void run()
		{
			DatagramPacket recvPacket = new DatagramPacket(new byte[512], 512);
			while (true)
			{
				try
				{
					recvPacket.setLength(512);
					socket.receive(recvPacket);
					text.append(new String(recvPacket.getData(), 0, recvPacket.getLength()));
					text.append("\n");
				}
				catch (SocketTimeoutException e) { /*no packet */ }
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(null, "I/O-error receiving packet");
					System.exit(-1);
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent event)
	{
		String line = inputField.getText();
		if (line.length()>0) line = userID+": "+line;
		sendPacket.setData(line.getBytes());
		sendPacket.setLength(line.length());
		try
		{
			socket.send(sendPacket);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "I/O-error while sending packet");
			System.exit(-1);
		}
		inputField.setText("");
		if (line.equals("") || line.equals("exit"))
		{
			JOptionPane.showMessageDialog(this, "Stopping program");
			System.exit(0);
		}
	}
}