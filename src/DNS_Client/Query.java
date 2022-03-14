package dns_Client;

import java.io.IOException;
import java.net.*;

public class Query 
{
	public static void main(String[] args)
	{
		DatagramSocket socket;
		try
		{
			socket = new DatagramSocket();
			socket.setSoTimeout(500);
			DatagramPacket packet = new DatagramPacket(new byte[512], 512);
			int id = buildDNSQuery(packet, "www.hs-kempten.de");
			packet.setPort(53);
			packet.setAddress(InetAddress.getByAddress(new byte[] { (byte) 208, 67, (byte)222, (byte)222 } ));
			System.out.println("--- Contacting server with query id "+id);
			socket.send(packet);
			packet.setLength(512);
			int waits = 0;
			while (true)
			{
				try
				{
					socket.receive(packet);
					evaluateDNSAnswer(packet.getData(), packet.getLength());
					break;
				}
				catch (SocketTimeoutException e)
				{
					System.out.print('.');
					if (++waits>50)
					{
						System.out.println("no reply, giving up");
						break;
					}
				}
			}
		}
		catch (ConnectException	e)	{ System.err.println("could not connect: "+e.getMessage());	}
		catch (SocketException	e)	{ System.err.println("timed out");							}
		catch (IOException		e)	{ System.err.println("exc: "+e.getMessage());				}
	}

	private static int buildDNSQuery(DatagramPacket packet, String query)
	{
		int id;
		packet.setLength(0);
		/* header */
		appendShort(packet, id=(int) (Math.random()*65536)); // random id
		appendShort(packet, 0b0000000100000000); //flags
		appendShort(packet, 1); //question count
		appendShort(packet, 0); //answer count
		appendShort(packet, 0); //name server records
		appendShort(packet, 0); //additional records
		/* question */
		// name consists of labels, each label has 8 bit length and label string
		String[] labels = query.split("\\.");
		for (String label: labels)
		{
			int labelLen = label.length();
			appendByte(packet, labelLen);
			for (int i = 0; i < labelLen; ++i) appendByte(packet, label.charAt(i));
		}
		appendByte(packet, 0); // zero length label for root
		appendShort(packet, 1); // query type: always use type A for IPv4 address
		appendShort(packet, 1); // class: always use class IN for internet
		return id;
	}

	private static void appendByte(DatagramPacket packet, int data) {
		// append 8 bits
		byte[] buf = packet.getData();
		int pos = packet.getLength();
		buf[pos++] = (byte) data;
		packet.setLength(pos);
	}

	private static void appendShort(DatagramPacket packet, int data)
	{
		// append 16 bits big endian
		appendByte(packet, data>>>8);
		appendByte(packet, data);
	}
	
	private static void evaluateDNSAnswer(byte[] data, int length)
	{
		System.out.println("received "+length+" bytes reply for id "+getShort(data,0));
		// check flags --> answer && no error
		int flags = getShort(data,2);
		if ((flags&0x8000) == 0 || (flags&0x000F) != 0)
		{
			System.out.println("flags indicate invalid answer: "+Integer.toHexString(flags));
			return;
		}
		int qdcount = getShort(data,4);
		int ancount = getShort(data,6);
		if (qdcount != 1 || ancount < 1)
		{
			System.out.println("packet contains not our question or no answers");
			return;
		}
		int pos = 12; // start with evaluation after header at byte 12
		// skip over query
		pos = skipName(data, pos); // skip name
		pos += 4; // skip type and class
		// read answers
		while (ancount-- > 0) pos = evaluateDNSAnswerRecord(data,pos);
	}

	private static int evaluateDNSAnswerRecord(byte[] buf, int pos)
	{
		/* resource */
		System.out.println("--- answer record ---");
		pos = skipName(buf, pos); //skip name
		System.out.println("type: "+getShort(buf, pos)); pos += 4; //type and class
		System.out.println("ttl: "+getInt(buf,pos)/60+" minutes"); pos += 4; //ttl
		int rdlen = getShort(buf, pos); pos += 2;
		System.out.print(rdlen+" data bytes: ");
		while (rdlen-- > 0) System.out.print(getByte(buf, pos++)+" ");
		System.out.println();
		return pos;
	}

	private static int getByte	(byte[] buf, int pos) { return buf[pos] & 0xFF;									}

	private static int getInt	(byte[] buf, int pos) { return getShort(buf, pos)<<16 + getShort(buf, pos+2);	}
	
	private static int getShort	(byte[] buf, int pos) { return (getByte(buf, pos)<<8) + getByte(buf, pos+1);	}

	private static int skipName	(byte[] buf, int pos)
	{
		int labelLen;
		boolean anotherLabel = true;
		do
		{
			labelLen = getByte(buf, pos++);
			if (labelLen == 0) anotherLabel = false; //end of label
			else if ((labelLen&0xC0)==0xC0)
			{
				++pos;
				anotherLabel = false;
			}
			else
				pos += labelLen;
		} while (anotherLabel);
		return pos;
	}
}