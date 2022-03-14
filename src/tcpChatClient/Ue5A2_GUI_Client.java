package tcpChatClient;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class Ue5A2_GUI_Client extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final static String APPNAME = "Chat and Paper";
	private JTextPane textPane;
	private final DefaultStyledDocument userStyleDoc;
	private final DefaultStyledDocument textStyleDoc;
	private JScrollPane textScroll;
	private int chatPaneLength;
	private JTextPane userPane;
	private JScrollPane userScroll;
	private JTextField inputField;
	private JButton sendButton;
	public final static int SERVERPORT = 4711;
	private Socket socket;
	private String userID;
	private BufferedWriter out;
	private ArrayList<String> users = new ArrayList<String>();
	
	private StyledDocument guiStyle;
	
	private final static String USERCODE = "_";
	private final static String MESSAGECODE = "---";
	private final static String WHISPERCODE = "/w";
	private final static String ERRORCODE = "/!";
	private final static String EXCLAMATIONCODE = "/em";
	private final static String DARKCODE = "/dm";
	
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
				new Ue5A2_GUI_Client(server, user, preparedSocket);
			}
		});
	}
	
	public Ue5A2_GUI_Client(String server, String user, Socket preparedSocket) {
		super(APPNAME+" ("+user+" at "+server+")");
		userID = user;
		socket = preparedSocket;
		String line = userID;
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write(line);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "I/O-error while sending packet");
			System.exit(-1);
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		guiStyle = new StyledDocument();
		userStyleDoc = new DefaultStyledDocument(guiStyle.getStyleContext());
		textStyleDoc = new DefaultStyledDocument(guiStyle.getStyleContext());
		
		textPane = new JTextPane(textStyleDoc);
		textPane.setEditable(false);
		chatPaneLength = 0;
		getContentPane().add(textScroll = new JScrollPane(textPane), BorderLayout.CENTER);
		textScroll.setPreferredSize(new Dimension(400, 300));
		textScroll.setAutoscrolls(true);
		textScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		userPane = new JTextPane(userStyleDoc);
		userPane.setBackground(guiStyle.getElementColor("userPane", "BG"));
		userPane.setEditable(false);
		getContentPane().add(userScroll = new JScrollPane(userPane), BorderLayout.EAST);
		userScroll.setPreferredSize(new Dimension(200, 300));
		userScroll.setAutoscrolls(true);
		userScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.setBorder(new LineBorder(Color.BLACK));
		bottom.add(inputField = new JTextField(), BorderLayout.CENTER);
		inputField.setBackground(guiStyle.getElementColor("inputField", "BG"));
		inputField.addActionListener(this);
		bottom.add(sendButton = new JButton("send"), BorderLayout.EAST);
		sendButton.addActionListener(this);
		add(bottom, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		inputField.requestFocus();
		(new GUIClientUpdater()).start();
	}
	
	private class GUIClientUpdater extends Thread
	{
		public void run()
		{
			BufferedReader in;
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e1) { return; }
			while (true)
			{
				try
				{
					String line;
					if ((line=in.readLine())!=null)
					{
						if(!line.equals(""))
						{
							if (line.startsWith(USERCODE))
							{
								String[] split = line.split(USERCODE);
								String otherUser = split[1];
								if(users.contains(otherUser))
									users.remove(otherUser);
								else
								{
									users.add(otherUser);
									try {
										userStyleDoc.insertString(userStyleDoc.getLength(), otherUser+"\n", otherUser.equals(userID) ? guiStyle.getStyle("user") : guiStyle.getStyle("standard"));
									} catch (BadLocationException e) {
										JOptionPane.showMessageDialog(null, "BLE");
									}
								}
								UpdateChatUsers();
								AddText(line.replace(USERCODE, ""), guiStyle.getStyle("chat"));
							}
							else if (line.startsWith(MESSAGECODE+WHISPERCODE))
								AddText(line.substring(MESSAGECODE.length()+WHISPERCODE.length()), guiStyle.getStyle("whisper"));
							else if (line.startsWith(MESSAGECODE+ERRORCODE))
								AddText(line.substring(MESSAGECODE.length()+ERRORCODE.length()), guiStyle.getStyle("error"));
							else if (line.startsWith(MESSAGECODE+EXCLAMATIONCODE))
								AddText(line.substring(MESSAGECODE.length()+EXCLAMATIONCODE.length()), guiStyle.getStyle("exclamation"));
							else if (line.startsWith(MESSAGECODE))
								AddText(line.substring(MESSAGECODE.length()), guiStyle.getStyle("header"));
							else
								AddText(line, guiStyle.getStyle("standard"));
						}
					}
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
	
	public void UpdateChatUsers()
	{
		try {
			userStyleDoc.remove(0, userStyleDoc.getLength());
			userStyleDoc.insertString(0, "USER IN CHAT:\n\n", guiStyle.getStyle("header"));
			for (String user : users)
			{
				userStyleDoc.insertString(userStyleDoc.getLength(), user+"\n", user.equals(userID) ? guiStyle.getStyle("user") : guiStyle.getStyle("standard"));
			}
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(null, "BLE");
		}
	}
	
	public void AddText(String message, Style style)
	{
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                		textStyleDoc.insertString(chatPaneLength, message+"\n", style);
                    } catch (BadLocationException e) { JOptionPane.showMessageDialog(null, "BLE: "+chatPaneLength); }
                    chatPaneLength += message.length() + 1;
                }
            });
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(null, "Exception when adding Text: " + e);
        }
	}
	
	public void actionPerformed(ActionEvent event)
	{
		String line = inputField.getText();
		if (line.equals(DARKCODE))
		{
			inputField.setText("");
			SwapTheme();
			return;
		}
		try {
			out.write(line);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "I/O-error while sending packet");
			System.exit(-1);
		}
		inputField.setText("");
		if (line.equals(""))
		{
			JOptionPane.showMessageDialog(this, "Stopping program");
			System.exit(0);
		}
	}
	
	private void SwapTheme()
	{
		guiStyle.SwapTheme();
		UpdateChatUsers();
		UpdateChat();
		inputField.setBackground(guiStyle.getElementColor("inputField", "BG"));
		inputField.setForeground(guiStyle.getElementColor("inputField", "FG"));
		sendButton.setBackground(guiStyle.getElementColor("button", "BG"));
		sendButton.setForeground(guiStyle.getElementColor("button", "FG"));
		userPane.setBackground(guiStyle.getElementColor("userPane", "BG"));
		textPane.setBackground(guiStyle.getElementColor("textPane", "BG"));
		textScroll.getVerticalScrollBar().setBackground(guiStyle.getElementColor("scrollBar", "BG"));
		userScroll.getVerticalScrollBar().setBackground(guiStyle.getElementColor("scrollBar", "BG"));
	}
	
	private void UpdateChat()
	{
		for (int i = 0; i < textStyleDoc.getLength(); ++i)
		{
			textStyleDoc.setLogicalStyle(i, guiStyle.complement(textStyleDoc.getCharacterElement(i).getAttributes()));
		}
	}
}