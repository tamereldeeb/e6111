package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Scanner;

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
	Double precisionTarget;
	String query;
	int round = 1;
	
	public BingService(String accountKey, String precisionTarget, String query) {
		this.accountKey = accountKey;
		this.precisionTarget = Double.parseDouble(precisionTarget);
		this.query = query;
	};
	
	public Boolean call() throws IOException {
		System.out.println("Parameters:");
		System.out.println("Client key: " + accountKey);
		System.out.println("Query: " + query);
		System.out.println("Precision: " + precisionTarget);

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

			int resultSize = feed.getEntries().size();

			System.out.println("Total no of results : " + resultSize);
			System.out.println("Bing Search Results:");
			System.out.println("======================");

			if (round == 1 && resultSize < 10) {
				System.out.println("Too few results returned, terminated.");
				return false;
			}

			// process results
			double relevant = 0.0;
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
						System.out.println("");

						// ask for user input
						Scanner reader = new Scanner(System.in);
						System.out.print("Relevant (Y/N)?");
						if (reader.next().toLowerCase().equals("y")) {
							relevant++;
						}
					}
				}
			}

			Double precision =  relevant/counter;
			System.out.println("======================");
			System.out.println("FEEDBACK SUMMARY");
			System.out.println("Query: " + query);
			System.out.println("Precision: " + new DecimalFormat("#0.0").format(precision));

			if (precision < precisionTarget) {
				System.out.println("Still below the desired precision of " + precisionTarget);

				if (precision < 0.1) {
					System.out.println("Below desired precision, but can no longer augment the query");
					return false;
				}

				// TODO figure out query augmentation
				System.out.println("Indexing results...");
				String augment = "TODO TODO";

				System.out.println("Augmenting by " + augment);
				this.query += " " + augment;
				this.round++;
				return true;
			}

			System.out.println("Desired precision reached, done");
			return false;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private String buildUrl(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27");
		try {
			sb.append(URLEncoder.encode(query, "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sb.append("%27&$top=10&$format=Atom");
		return sb.toString();
	}
	
}