import java.io.IOException;

import javax.naming.ldap.StartTlsRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
public class WebScrape {
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
	
	public static void main (String[] args) {
		Document doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/British_Columbia.html");
		Elements dataElements = getValidVolunteerData(doc);
		
	}
}
