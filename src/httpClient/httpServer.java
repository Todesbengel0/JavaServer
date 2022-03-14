package httpClient;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;

public class httpServer
{
	final static int SERVERPORT = 8000;
	
	public static void main(String[] args) throws IOException
	{
		HttpServer server = HttpServer.create(new InetSocketAddress(SERVERPORT), 10);
		HttpContext context = server.createContext("/");
		context.setHandler(httpServer::handleRequest);
		server.start();
	}

	private static void handleRequest(HttpExchange exchange) throws IOException
	{
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET"))
		{
			Headers responseHeaders = exchange.getResponseHeaders();
			OutputStream responseBody = exchange.getResponseBody();
			Headers requestHeaders = exchange.getRequestHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200,0);
			Set<String> keySet = requestHeaders.keySet();
			Iterator<String> iter = keySet.iterator();
			
			responseBody.write("Hello out there\n".getBytes());
			responseBody.write(("You asked for "+exchange.getRequestURI()+"\n").getBytes());
			responseBody.write("You sent the following header options:\n\n".getBytes());
			
			while (iter.hasNext())
			{
				String key = iter.next();
				List<?> values = requestHeaders.get(key);
				String s = key + " = " + values.toString() + "\n";
				responseBody.write(s.getBytes());
			}
			responseBody.close();
		}
	}
}