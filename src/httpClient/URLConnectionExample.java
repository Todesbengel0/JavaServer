package httpClient;
import java.io.*;  
import java.net.*; 

public class URLConnectionExample {
	public final static String SERVERNAME = "http://www.tombleek.de";
	public static void main(String[] args){  
		try{  
			URL url = new URL(SERVERNAME);  
			URLConnection	urlcon = url.openConnection();  
			InputStream		stream = urlcon.getInputStream();  
			int i;  
			while((i=stream.read())!=-1)
			{  
				System.out.print((char)i);  
			} 
			stream.close();
		}
		catch(Exception e){ System.out.println(e); }  
	}  
}  