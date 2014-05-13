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
	
	public static void createDataObjects (Elements dataElements) {
		for (Element dataElement : dataElements) {
			Document elementDoc = getDocumentFromUrl(dataElement.attr("abs:href"));
			String docName = elementDoc.title().toString().substring(0, elementDoc.title().toString().indexOf(":"));
			volunteerData.add(new VolunteerData(dataElement.attr("abs:href"), getElementAddress(elementDoc), null, docName));
		}
	}
	
	public static String getElementCategory(Document doc) {
		Elements categoryElements = doc.select("td[width$=645]");
		String containsKeyword = categoryElements.first().toString();
		String category = containsKeyword.substring(containsKeyword.lastIndexOf("Keywords"), containsKeyword.length()-5);
		return category;
	}
	
	public static String getElementAddress (Document doc) {
		Elements addressElements = doc.getElementsByTag("tr");
		Element dataElement = null;
		String docName = doc.title().toString().substring(0, doc.title().toString().indexOf(":"));
		for (Element addressElement : addressElements) {
			if (addressElement.toString().contains("<b>" + docName + "</b><br />")) {
				dataElement = addressElement;
			}
		}
		String address = dataElement.toString().substring(dataElement.toString().indexOf("<br />") + 6);
		address = address.substring(0, address.indexOf("<br />"));
		return address;
	}
	
	public static void main (String[] args) {
		Document doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/Alberta.html");
		Elements dataElements = getValidVolunteerData(doc);
		createDataObjects(dataElements);
		for (VolunteerData data : volunteerData) {
			System.out.println(data.toString());
		}
	}

}
