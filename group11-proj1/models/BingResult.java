package models;

public class BingResult {

	String url;
	String title;
	String summary;
	
	public BingResult(String url, String title, String summary) {
		this.url=url;
		this.title=title;
		this.summary=summary;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSummary() {
		return summary;
	}
	
}