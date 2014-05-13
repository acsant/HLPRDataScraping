import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class VolunteerData {
	private String link = null;
	private String address = "";
	private String category = "";
	private String title = "";
	public VolunteerData (String link, String address, String category, String title) {
		this.link = link;
		this.address = address;
		this.category = category;
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	

}
