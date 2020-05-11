public class Star {
//	private String id;
	private String name;
	private int birthYear;

	public Star(String name) {
		this.name = name;
		birthYear = 0;
	}

	public Star(String name, int birthYear) {
		this.name = name;
		this.birthYear = birthYear;
	}

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Star Details - ");
		sb.append("name:").append(getName());
		sb.append(", ");
		sb.append("birthYear:").append(getBirthYear());
		return sb.toString();
	}
}
