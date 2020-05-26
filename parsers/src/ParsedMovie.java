import java.util.ArrayList;
import java.util.List;

public class ParsedMovie {
	private String title;
	private int year;
	private String director;
	private List<ParsedGenre> parsedGenres;

	public ParsedMovie(String title, int year, String director) {
		this.title = title;
		this.year = year;
		this.director = director;
		parsedGenres = new ArrayList<>();
	}

	public ParsedMovie(String title, int year, String director, List<ParsedGenre> parsedGenres) {
		this.title = title;
		this.year = year;
		this.director = director;
		this.parsedGenres = parsedGenres;
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

	public List<ParsedGenre> getParsedGenres() {
		return parsedGenres;
	}

	public void setParsedGenres(List<ParsedGenre> parsedGenres) {
		this.parsedGenres = parsedGenres;
	}

	public void addGenre(ParsedGenre parsedGenre) {
		this.parsedGenres.add(parsedGenre);
	}

	public void clearGenres() {
		this.parsedGenres.clear();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Movie{title:").append(getTitle());
		sb.append(", year:").append(getYear());
		sb.append(", director:").append(getDirector());
		sb.append(", genres:").append(getParsedGenres()).append("}");
		return sb.toString();
	}
}
