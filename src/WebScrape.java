import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class WebScrape {
	
	private static LinkedList<VolunteerData> volunteerData = new LinkedList<VolunteerData>();
	
	public WebScrape() {
		
	}
	
	public static Document getDocumentFromUrl (String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	public Element getElementsWithTag (String tag, Document doc) {
		Element elementByTag = doc.getElementById(tag);
		return elementByTag;
	}
	
	public static Elements getValidVolunteerData (Document doc) {
		Elements links = doc.getElementsByTag("a");
		Elements dataElements = new Elements();
		for (Element link : links) {
			if (link.attr("title").contains("Volunteer information")) {
				dataElements.add(link);
			}
		}
		return dataElements;
	}
	
	public void createDataObjects (Elements dataElements) {
		for (Element dataElement : dataElements) {
			Document elementDoc = getDocumentFromUrl(dataElement.attr("abs:href"));
		}
	}
	
	public static String getElementAddress(Document doc) {
		String address = "";
		Elements addressElement = doc.getElementsByTag("td");
		for (Element data : addressElement) {
			System.out.println(data.toString());
		}
		return address;
	}
	
	public static void main (String[] args) {
		Document doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/Alberta.html");
		Elements dataElements = getValidVolunteerData(doc);
		Document addressDoc = getDocumentFromUrl(dataElements.first().attr("abs:href"));
		String address = getElementAddress(addressDoc);
	}

}
