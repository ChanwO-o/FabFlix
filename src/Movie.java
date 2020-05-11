public class Movie {
	private String title;
	private int year;
	private String director;

	public Movie(String title, int year, String director) {
		this.title = title;
		this.year = year;
		this.director = director;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Movie Details - ");
		sb.append("title:").append(getTitle());
		sb.append(", ");
		sb.append("year:").append(getYear());
		sb.append(", ");
		sb.append("director:").append(getDirector());
		return sb.toString();
	}
}
