package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
			// connect to Bing service
			byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
			String accountKeyEnc = new String(accountKeyBytes);

			URL url = new URL(bingUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(urlConnection));

			System.out.println("Total no of results : " + feed.getEntries().size());
			System.out.println("Bing Search Results:");
			System.out.println("======================");

			// process results
			ArrayList<BingResult> results = new ArrayList<BingResult>();
			int counter = 0;
			for (Iterator<?> entryIter = feed.getEntries().iterator();entryIter.hasNext();) {
				System.out.println("Result " + ++counter);
				SyndEntry entry = (SyndEntry) entryIter.next();
				SyndContent content = (SyndContent) (entry.getContents().get(0));
				String xml = content.getValue();

				// parse feed content
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(xml));
				Document document = builder.parse(is);

				NodeList nList = document.getElementsByTagName("m:properties");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						BingResult result = new BingResult(eElement.getElementsByTagName("d:Url").item(0).getTextContent(), eElement.getElementsByTagName("d:Title").item(0).getTextContent(), eElement.getElementsByTagName("d:Description").item(0).getTextContent());
						System.out.println("[");
						System.out.println("  URL: " + result.getUrl());
						System.out.println("  Title: " + result.getTitle());
						System.out.println("  Summary: " + result.getSummary());
						System.out.println("]");
						results.add(result);
					}
				}
			}

			return results;

		} catch (Exception ex) {
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