package tcpChatClient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Ue5A1_Server extends Thread {
	public final static int PORT = 4711;
	private final static String SERVERNAME = "Chat and Paper";
	private static ArrayList<Ue5A1_Server> clientThreads = new ArrayList<Ue5A1_Server>();
	private final static String USERCODE = "_";
	private final static String MESSAGECODE = "---";
	private final static String WHISPERCODE = "/w";
	private final static String ERRORCODE = "/!";
	private final static String ROLLCODE = "/r";
	private final static String GMCODE = "/gm";
	private final static String GMROLLCODE = "/gr";
	private final static String GMWHISPERCODE = "/gw";
	private final static String EXCLAMATIONCODE = "/em";
	private final static String TOPICCODE = "/tp";
	private static Random randomNumber;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		randomNumber = new Random();
		try
		{
			serverSocket = new ServerSocket(PORT);
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
					Ue5A1_Server temp = new Ue5A1_Server(cnt++, client);
					temp.start();
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
	private String userID;
	private BufferedWriter out;
	private boolean isGM;
	private boolean whisperResponse;
	private String whisperMessage;
	private ArrayList<Ue5A1_Server> whisperTargets = new ArrayList<Ue5A1_Server>();
	private Ue5A1_Server(int number, Socket client)
	{
		threadNumber = number;
		localClient = client;
		userID = null;
		isGM = false;
		whisperTargets.clear();
		whisperResponse = false;
		whisperMessage = null;
	}
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(localClient.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(localClient.getOutputStream()));
			System.out.println("--- Started thread "+threadNumber);
			out.write(MESSAGECODE+"Welcome to "+SERVERNAME+" in thread "+threadNumber);
			out.newLine();
			out.flush();
			String line;
			
			if ((line=in.readLine())!=null)
			{
				userID = line;
				synchronized(clientThreads)
				{
					clientThreads.add(this);
					if (userID.equals(""))
					{
						Error("Username cannot be empty!");
						closeThread();
						return;
					}
					if (userID.startsWith(MESSAGECODE) || userID.contains(USERCODE))
					{
						Error("Username cannot start with "+MESSAGECODE+" or have "+USERCODE+" in its name!");
						closeThread();
						return;
					}
					int UserNameCount = 0;
					for (Ue5A1_Server clientThread : clientThreads)
					{
						if(clientThread != null)
						{
							if (userID.equals(clientThread.getUserID()))
								++UserNameCount;
						}
					}
					if(UserNameCount != 1)
					{
						Error("Username already in use!");
						closeThread();
						return;
					}
					
					for (Ue5A1_Server clientThread : clientThreads)
					{
						if(clientThread != null)
						{
							line = (USERCODE+clientThread.getUserID()+USERCODE+" has joined the chat.");
							out.write(line);
							out.newLine();
							out.flush();
							if (clientThread.getUserID() != userID)
							{
								line = (USERCODE+userID+USERCODE+" has joined the chat.");
								clientThread.out.write(line);
								clientThread.out.newLine();
								clientThread.out.flush();
							}
						}
					}
				}
				
				while ((line=in.readLine())!=null)
				{
					if(line.equals(""))
						break;
					System.out.println("--- "+threadNumber+" Received: "+line);
					if(whisperResponse)
						if (WhisperResponseHandle(line))
							continue;
					if(line.startsWith(WHISPERCODE))
						Whisper(line);
					else if (line.startsWith(GMWHISPERCODE))
						WhisperToGM(line);
					else if (line.startsWith(ROLLCODE))
						Roll(line, false);
					else if (line.startsWith(GMROLLCODE))
						Roll(line, true);
					else if (line.startsWith(EXCLAMATIONCODE))
						MessageAll(MESSAGECODE+EXCLAMATIONCODE+userID+line.substring(EXCLAMATIONCODE.length()));
					else if (line.startsWith(TOPICCODE))
						SendTopic(line);
					else if (line.equals(GMCODE))
					{
						isGM = true;
						out.write(MESSAGECODE+"You are now a Game Master!");
						out.newLine();
						out.flush();
					}
					else
						MessageAll(userID+": "+line);
				}
			}
			MessageAll(USERCODE+userID+USERCODE+" has left the chat.");
			closeThread();
		}
		catch (IOException e)
		{
			System.err.println("server thread "+threadNumber+": "+e.getMessage());
			closeThread();
			MessageAll(USERCODE+userID+USERCODE+" has left the chat.");
		}
	}
	public String getUserID()
	{
		return userID;
	}
	public Socket getLocalClient()
	{
		return localClient;
	}
	public boolean getIsGM()
	{
		return isGM;
	}
	public void closeThread()
	{
		System.out.println("--- "+threadNumber+" Client shutdown");
		try {
			synchronized(clientThreads)
			{
				clientThreads.remove(this);
			}
			localClient.close();
		} catch (IOException e) { }
	}
	public void MessageAll(String message)
	{
		if (message.equals(""))
			return;
		try {
			synchronized(clientThreads)
			{
				for (Ue5A1_Server clientThread : clientThreads)
				{
					if(clientThread != null)
					{
						clientThread.out.write(message);
						clientThread.out.newLine();
						clientThread.out.flush();
					}
				}
			}
		} catch (IOException e1) {
			System.out.println("server thread "+threadNumber+" - MessageError: "+e1.getMessage());
		}
	}
	
	private void Whisper(String message)
	{
		try
		{
			String[] spaceSplit = message.split(" ");
			String userMessage = message.substring(WHISPERCODE.length() + 1);
			
			if (spaceSplit.length <= 2)
			{
				Error("Wrong Whisper Format. Please use /w [UserName] [Message] without the brackets.");
				return;
			}
			
			synchronized(clientThreads)
			{
				for (Ue5A1_Server clientThread : clientThreads)
				{
					if (userMessage.startsWith(clientThread.getUserID()) && userMessage.substring(clientThread.getUserID().length()).length() > 1)
					{
						whisperTargets.add(clientThread);
					}
				}
			}
			if (whisperTargets.isEmpty())
			{
				Error("There is no user with this username in the chat or the message is empty.");
				return;
			}
			
			if (whisperTargets.size() > 1)
			{
				whisperResponse = true;
				whisperMessage = userMessage;
				Error("There are two or more users you could have addressed with this message. Type the number of the user in the chat that you want to address.");
				int i = 1;
				for (Ue5A1_Server target : whisperTargets)
					Error((i++)+": "+target.getUserID());
				return;
			}
			
			userMessage = userMessage.substring(whisperTargets.get(0).getUserID().length());
			out.write(MESSAGECODE+WHISPERCODE+"you whisper to "+whisperTargets.get(0).getUserID()+":"+userMessage);
			out.newLine();
			out.flush();
			whisperTargets.get(0).out.write(MESSAGECODE+WHISPERCODE+userID+" whispers to you:"+userMessage);
			whisperTargets.get(0).out.newLine();
			whisperTargets.get(0).out.flush();
			
			whisperTargets.clear();
		}
		catch (IOException e) { }
	}
	
	private boolean WhisperResponseHandle(String message)
	{
		boolean responded = false;
		try
		{
			int index = Integer.parseInt(message) - 1;
			String userMessage = whisperMessage.substring(whisperTargets.get(index).getUserID().length());
			out.write(MESSAGECODE+WHISPERCODE+"you whisper to "+whisperTargets.get(index).getUserID()+":"+userMessage);
			out.newLine();
			out.flush();
			whisperTargets.get(index).out.write(MESSAGECODE+WHISPERCODE+userID+" whispers to you:"+userMessage);
			whisperTargets.get(index).out.newLine();
			whisperTargets.get(index).out.flush();
			responded = true;
		}
		catch (IOException e)
		{
			Error("There was an error processing your command.");
		}
		catch (NumberFormatException e) { }
		catch (IndexOutOfBoundsException e) { }
		whisperResponse = false;
		whisperTargets.clear();
		whisperMessage = null;
		return responded;
	}
	
	private void WhisperToGM(String message)
	{
		try
		{
			String outMessage = message.substring(GMWHISPERCODE.length());
			
			if (outMessage.equals(""))
			{
				Error("Wrong Whisper Format. Please use /gw [Message] without the brackets.");
				return;
			}
			
			out.write(MESSAGECODE+WHISPERCODE+"you whisper to gm:"+outMessage);
			out.newLine();
			out.flush();
			synchronized(clientThreads)
			{
				for (Ue5A1_Server clientThread : clientThreads)
				{
					if (clientThread.getIsGM())
					{
						clientThread.out.write(MESSAGECODE+WHISPERCODE+userID+" whispers to gm:"+outMessage);
						clientThread.out.newLine();
						clientThread.out.flush();
					}
				}
			}
		}
		catch (IOException e) { }
	}
	
	private void Roll(String message, boolean toGM)
	{
		String[] spaceSplit = message.split(" ");
		String[] diceNumbers = spaceSplit[1].split("d");
		if (spaceSplit.length <= 1 || diceNumbers.length <= 1 || diceNumbers.length > 3)
		{
			Error("Wrong Roll Format. Please use "+ROLLCODE+" [NumberOfRolls]d[DiceNumber]d[NumberOfDitches] without the brackets.");
			return;
		}
		int numberOfRolls = 0;
		int diceType = 0;
		try
		{
			numberOfRolls = Integer.parseInt(diceNumbers[0]);
			diceType = Integer.parseInt(diceNumbers[1]);
		}
		catch (NumberFormatException e)
		{
			Error("Wrong Roll Format. Please use "+ROLLCODE+" [NumberOfRolls]d[DiceNumber]d[NumberOfDitches] without the brackets.");
			return;
		}
		
		if (numberOfRolls <= 0 || diceType <= 0)
		{
			Error("Please enter values greater than 0.");
			return;
		}
		
		int[] rolls = new int[numberOfRolls];
		
		int sum = 0;
		
		for (int i = 0; i < numberOfRolls; ++i)
		{
			rolls[i] = randomNumber.nextInt(diceType - 1) + 1;
			sum += rolls[i];
		}
		
		if (diceNumbers.length == 3)
		{
			int ditchCount = Integer.parseInt(diceNumbers[2]);
			if(ditchCount > numberOfRolls)
			{
				Error("Number of rolls to ditch may not be greater than the total number of rolls.");
				return;
			}
			int[] temp = new int[numberOfRolls];
			System.arraycopy(rolls, 0, temp, 0, numberOfRolls);
			for (int i = 0; i < ditchCount; ++i)
			{
				int index = -1;
				int value = -1;
				for (int j = 0; j < numberOfRolls; ++j)
				{
					if (temp[j] == -1)
						continue;
					if (index == -1 || value > temp[j])
					{
						index = j;
						value = temp[j];
					}
				}
				sum -= value;
				temp[index] = -1;
			}
		}
		
		if (spaceSplit.length >= 3)
		{
			if (spaceSplit.length != 4)
			{
				Error("Wrong Mathematics Format. Please use "+ROLLCODE+" [RollQuery] [Operator] [Number] without the brackets.");
				return;
			}
			
			int operand = Integer.parseInt(spaceSplit[3]);
			System.out.println("--- Berechnung der Summe: "+sum+" mit Wert: "+operand);
			
			switch(spaceSplit[2])
			{
			case "+":
				System.out.println("--- Addition");
				sum += operand;
				break;
			case "-":
				sum -= operand;
				break;
			case "*":
				sum *= operand;
				break;
			default:
				Error("Wrong Operator. Please use +, - or *.");
				return;
			}
			System.out.println("--- Neue Summe: "+sum);
		}
		
		String outMessage = ": ( ";
		for (int i = 0; i < numberOfRolls; ++i)
		{
			if (i > 0)
				outMessage += ", ";
			outMessage += rolls[i] + " ";
		}
		outMessage += ") ";
		if (spaceSplit.length == 4)
			outMessage += spaceSplit[2] + " " + spaceSplit[3];
		outMessage += " = " + sum;
		
		if (!toGM)
		{
			outMessage = userID + ":" + message.substring(ROLLCODE.length()) + outMessage;
			MessageAll(outMessage);
			return;
		}
		
		outMessage =  GMWHISPERCODE + message.substring(GMROLLCODE.length()) + outMessage;
		WhisperToGM(outMessage);
	}
	
	private void SendTopic(String message)
	{
		if(!isGM)
		{
			Error("You need to be a Game Master to use this command.");
			return;
		}
		MessageAll(MESSAGECODE+message.substring(TOPICCODE.length() + 1));
	}
	
	private void Error(String message)
	{
		try
		{
			out.write(MESSAGECODE+ERRORCODE+message);
			out.newLine();
			out.flush();
		}
		catch (IOException e) { }
	}

}
