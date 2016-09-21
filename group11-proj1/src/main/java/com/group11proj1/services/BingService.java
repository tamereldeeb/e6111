package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;

public class BingService {

	String accountKey;
	String precision;
	String query;
	
	public BingService(String accountKey, String precision, String query) {
		this.accountKey = accountKey;
		this.precision = precision;
		this.query = query;
	};
	
	public ArrayList<BingResult> call() throws IOException {

		String bingUrl = buildUrl(query);
		System.out.println("URL: " + bingUrl);
		
		try {
			// query Bing
			byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
			String accountKeyEnc = new String(accountKeyBytes);

			URL url = new URL(bingUrl);
			 URLConnection urlConnection = url.openConnection();
			 urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			 InputStream inputStream = (InputStream) urlConnection.getContent();
			 byte[] contentRaw = new byte[urlConnection.getContentLength()];
			 inputStream.read(contentRaw);
			 String content = new String(contentRaw);
			
			//TODO parse results
			System.out.println("======================");
			 System.out.println(content);
			// return parseResults(content);
			return new ArrayList<BingResult>();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private String buildUrl(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27");
		sb.append(StringEscapeUtils.escapeHtml4(query));
		sb.append("%27&$top=10&$format=Atom");
		return sb.toString();
	}
	
}