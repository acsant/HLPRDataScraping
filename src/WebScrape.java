import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.HttpStatusException;
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
			volunteerData.add(new VolunteerData(dataElement.attr("abs:href"), getElementAddress(elementDoc), getElementCategory(elementDoc), docName));
		}
	}

	public static String getElementCategory(Document doc) {
		Elements categoryElements = doc.select("table[width$=640]");
		String containsKeyword = categoryElements.first().toString();
		String category = containsKeyword.substring(containsKeyword.lastIndexOf("Keywords"), containsKeyword.lastIndexOf("</td>"));
		return category;
	}

	public static String getElementAddress (Document doc) {
		Elements addressElements = doc.getElementsByTag("tr");
		Element dataElement = null;
		String dataToManipulate = "";
		String docName = doc.title().toString().substring(0, doc.title().toString().indexOf(":"));
		if (docName.contains("&")) {
			docName = docName.replace("&", "&amp;");
		}
		for (Element addressElement : addressElements) {
			if (addressElement.toString().contains("<b>" + docName ) ||
					addressElement.toString().contains("<b>" + docName + "</b><br />")) {
				dataElement = addressElement;
			}
		}
		if (dataElement.toString().contains("Branch")) {
			dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("Branch"));
			dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />"));
		} else if (dataElement.toString().contains("<b>" + docName + "<br />")) {
			dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("<b>" + docName + "<br />"));
			dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />") + 6);
			dataToManipulate = dataToManipulate.substring(dataToManipulate.indexOf("<br />") + 6);
		} else {
			dataToManipulate = dataElement.toString().substring(dataElement.toString().indexOf("<br />") + 6);
		}
		String address = dataToManipulate;
		address = address.substring(0, address.indexOf("<br />"));
		return address;
	}

	public static Elements filterDataElements(Elements dataElements){
		Elements filter = new Elements();
		for (Element dataElement : dataElements) {
			if (!dataElement.attr("abs:href").equals("http://www.canadian-universities.net/Volunteer/Centre-daction-benevole-et-communautaire-Saint-Laurent.html") &&
					!dataElement.attr("abs:href").equals("http://www.canadian-universities.net/Volunteer/The-Duke-of-Edinburghs-Award.html")) {
				filter.add(dataElement);
			}
		}
		return filter;
	}

	public static void main (String[] args) {
		/*
		 *	args[0] = city
		 *	args[1] = province
		 *  args[2] = keyword 
		 */
		String province = "Northwest_Territories";
		Document doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/" + province + ".html");
		Document docForExtraction = null;
		Elements pageElems = doc.select("b[CLASS$=navigb]");
		String pages = "";
		int totalPages=1;
		if (pageElems.size() > 0) {
			pages = pageElems.last().text().substring(1, pageElems.last().text().length() - 1);
			totalPages = Integer.parseInt(pages);
		}
		for (int i = 1; i <= totalPages; i++) {
			if (i != 1) {
				doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/" + province + i + ".html");
			}
			Elements dataElements = getValidVolunteerData(doc);
			Elements filteredDataElements = filterDataElements(dataElements);
			try {
				createDataObjects(filteredDataElements);
			} catch (Exception e) {
				e.getMessage() ;
			}
		}

		for (VolunteerData dataElement : volunteerData) {
			System.out.println(dataElement.toString() + "\n");
		}
		 
	}

}
