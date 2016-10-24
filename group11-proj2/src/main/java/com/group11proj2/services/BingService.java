package com.group11proj2.services;

import com.group11proj2.models.BingResult;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BingService {

	String accountKey;
	
	public BingService(String accountKey) {
		this.accountKey = accountKey;
	};

	public String getKey() {
		return accountKey;
	}

	public Integer query(String website, String query) throws IOException {
		List<BingResult> results = new ArrayList<>();
		String bingUrl = buildUrl(website, query);

		try {
			// connect to Bing service
			byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
			String accountKeyEnc = new String(accountKeyBytes);

			URL url = new URL(bingUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(urlConnection));

			Iterator<?> entryIter = feed.getEntries().iterator();
			SyndEntry entry = (SyndEntry) entryIter.next();
			SyndContent content = (SyndContent) (entry.getContents().get(0));
			String xml = content.getValue();

			// parse feed content
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document document = builder.parse(is);

			NodeList nList = document.getElementsByTagName("m:properties");
			Node nNode = nList.item(0);
			Element eElement = (Element) nNode;
			Integer totalResults = Integer.parseInt(eElement.getElementsByTagName("d:WebTotal").item(0).getTextContent());

			return totalResults;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private String buildUrl(String website, String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.datamarket.azure.com/Bing/SearchWeb/v1/Composite?Query=%27site%3a");
		sb.append(website);
		sb.append("%20");
		try {

			sb.append(URLEncoder.encode(query, "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sb.append("%27&$top=10&$format=Atom");
		return sb.toString();
	}
	
}