package sample.hello;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HTTP {
	public static String GET(String url) {
	    try {
	        URLConnection conn = new URL(url).openConnection();

	        BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
	        StringBuilder sb = new StringBuilder();

	        byte[] buffer = new byte[1024];			        
	        int bytes = 0;
	        while ((bytes = input.read(buffer)) != -1) {
	        	sb.append(new String(buffer, 0, bytes));
	        }	        
	        input.close();

	        return sb.toString();
	    } catch (IOException e) {
	    	return "";
	    }
	}
	
	public static void POST(String url, Map<String, String> params) {
		try {
			byte[] body = buildBody(params);
			
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(body.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			OutputStream output = conn.getOutputStream();
			output.write(body);
			output.close();
		} catch (IOException e) { }
	}
	
	private static byte[] buildBody(Map<String, String> params) {
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		while(iterator.hasNext())
		{
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if(iterator.hasNext())
			{
				bodyBuilder.append('&');
			}
		}

		String body = bodyBuilder.toString();
		return body.getBytes();
	}
}
