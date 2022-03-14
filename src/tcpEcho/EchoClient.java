package tcpEcho;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

import javax.swing.*;

public class EchoClient extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextArea text;
	private JTextField inputField;
	private JButton sendButton;
	public final static int SERVERPORT = 4711;
	private Socket socket;
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
				Socket preparedSocket = new Socket(); 
				try
				{
					preparedSocket.setSoTimeout(50);
					preparedSocket.connect(new InetSocketAddress(InetAddress.getByName(server), SERVERPORT), 1000);
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
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(null, "IOException creating socket: "+e.toString());
					System.exit(-1);
				}
				new EchoClient(server, user, preparedSocket);
			}
		});
	}
	
	public EchoClient(String server, String user, Socket preparedSocket) {
		super("ChatClient ("+user+" at "+server+")");
		userID = user;
		socket = preparedSocket;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(text = new JTextArea(), BorderLayout.CENTER);
		text.setPreferredSize(new Dimension(400, 300));
		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.add(inputField = new JTextField(), BorderLayout.CENTER);
		inputField.addActionListener(this);
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
			while (true)
			{
				try
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					text.append(in.readLine());
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
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write(line);
			out.newLine();
			out.flush();
		} catch (IOException e) {
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