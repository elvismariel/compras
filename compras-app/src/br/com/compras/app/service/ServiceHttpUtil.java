package br.com.compras.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceHttpUtil {
	
	public static boolean requestPost(String restUrl, String restJson){
		
		HttpURLConnection conn = null;
		StringBuffer chaine = new StringBuffer("");
		boolean ret = false;
		
		try {
			URL url = new URL(restUrl);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(restJson);
			out.flush();
			out.close();
 
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
 
			String output;
			while ((output = br.readLine()) != null) {
				chaine.append(output);
			}
			
			ret = Boolean.parseBoolean(chaine.toString());
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			conn.disconnect();
		}
		return ret;
	}
	
	public static String post(String restUrl, String restJson){
		
		HttpURLConnection conn = null;
		StringBuffer chaine = new StringBuffer("");
		
		try {
			URL url = new URL(restUrl);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(restJson);
			out.flush();
			out.close();
 
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
 
			String output;
			while ((output = br.readLine()) != null) {
				chaine.append(output);
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			conn.disconnect();
		}
		return chaine.toString();
	}
}
