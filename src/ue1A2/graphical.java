package ue1A2;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
//import java.util.Scanner;
import javax.swing.*;

public class graphical extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea text;
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		final String server, user;
		if ((server=(String)JOptionPane.showInputDialog(null,"Please enter your chat server",
				"ChatClient", JOptionPane.QUESTION_MESSAGE, null, null, "localhost"))==null
				|| "".equals(server)) return;
		if ((user=(String)JOptionPane.showInputDialog(null,"Please eneter your user name",
				"ChatClient", JOptionPane.QUESTION_MESSAGE, null, null, "smf"))==null
				|| "".equals(user)) return;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try {
					new graphical(server, user);
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public graphical(String server, String user) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		setTitle("ChatCryEnd from "+server+" in "+user);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		add(text = new JTextArea(), BorderLayout.CENTER);
		
		File audioFile = new File("I:\\Documents\\Spiele\\DnD\\Dragon of Icespire Peak\\conspiracy.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip audioClip = (Clip) AudioSystem.getLine(info);
		audioClip.open(audioStream);
		audioClip.start();
		
		setSize(450,420);
		//setResizable(false);
		setAlwaysOnTop(true);
		
		p.setLayout(new BorderLayout());
		p.add(new JTextField("Don't type here!"), BorderLayout.CENTER);
		p.add(new JButton("Don't hit me"), BorderLayout.EAST);
		p.setSize(450,320);
		p.setBackground(Color.darkGray);
		p.setVisible(true);
		add(p, BorderLayout.SOUTH);
		
		text.setText("This is an editable JTextArea. " +
			    "But please do not edit me! " +
			    "You will regret it! " +
			    "Don't do it!"
		);
		text.setBounds(100, 50, 100, 20);
		text.setPreferredSize(new Dimension(200, 150));
		text.setFont(new Font("Serif", Font.ITALIC, 16));
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setBackground(Color.gray);
		text.setSelectedTextColor(Color.darkGray);
		text.setForeground(Color.white);
		
		pack();
		setVisible(true);
		
		/*audioClip.close();
		audioStream.close();*/

	}

}
