
public class VolunteerData {
	private String link = null;
	private String address = "";
	private String category = "";
	private String name = "";
	public VolunteerData (String link, String address, String category, String name) {
		this.link = link;
		this.address = address;
		this.category = category;
		this.name = name;
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
	
	public String toString() {
		return "Information for: " + name +
				"\nAddress: " + address +
				"\n" + category;
	}

}
