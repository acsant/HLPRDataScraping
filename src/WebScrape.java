import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class WebScrape {

	private static LinkedList<VolunteerData> volunteerData = new LinkedList<VolunteerData>();
	private static int dataCount = 0;

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
			System.out.println(volunteerData.getLast().toString() + "\n");
			dataCount ++;
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
			if (!dataElement.attr("abs:href").equals("http://www.c`anadian-universities.net/Volunteer/Centre-daction-benevole-et-communautaire-Saint-Laurent.html") &&
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
		LinkedList<String> provinceList = new LinkedList<String>();
		LinkedList<VolunteerData> results = new LinkedList<VolunteerData>();
		String city = "";
		String keyword = "";
		provinceList.add("Alberta");
		provinceList.add("British_Columbia");
		provinceList.add("Manitoba");
		provinceList.add("New_Brunswick");
		provinceList.add("Newfoundland");
		provinceList.add("North_York");
		provinceList.add("Northwest_Territories");
		provinceList.add("Nova_Scotia");
		provinceList.add("Nunavut");
		provinceList.add("Ontario");
		provinceList.add("Prince_Edward_Island");
		provinceList.add("Quebec");
		provinceList.add("Saskatchewan");
		provinceList.add("Yukon");

		for (String province : provinceList) {
			Document doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/" + province + ".html");
			Elements pageElems = new Elements();
			String pages = "";
			try {
				pageElems = doc.select("b[CLASS$=navigb]");
			} catch (Exception e) {
				System.out.println("");
			}
			int totalPages=1;
			if (pageElems.size() > 0) {
				pages = pageElems.last().text().substring(1, pageElems.last().text().length() - 1);
				totalPages = Integer.parseInt(pages);
			}
			for (int i = 1; i <= totalPages; i++) {
				if (i != 1) {
					doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/" + province + i + ".html");
				} else {
					doc = getDocumentFromUrl("http://www.canadian-universities.net/Volunteer/" + province + ".html");
				}
				Elements dataElements = getValidVolunteerData(doc);
				//Elements filteredDataElements = filterDataElements(dataElements);
				Elements filteredDataElements = dataElements;
				try {
					createDataObjects(filteredDataElements);
				} catch (Exception e) {
					e.getMessage() ;
				}
			}
			for (VolunteerData data : volunteerData) {
				results.add(data);
			}
			if (!city.isEmpty()) {
				for (VolunteerData dataElement : volunteerData) {
					if (!dataElement.getAddress().contains(city)) {
						results.remove(dataElement);
					}
				}
			}

			if (!keyword.isEmpty()) {
				for (VolunteerData dataElement : volunteerData) {
					if (!dataElement.getCategory().contains(keyword)) {
						results.remove(dataElement);
					}
				}
			}
			/*for (VolunteerData dataElement : results) {
			System.out.println(dataElement.toString() + "\n");
		} */
		}
		System.out.println("The data count is: " + dataCount);
	}


}
