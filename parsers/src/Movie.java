import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String title;
	private int year;
	private String director;
	private List<Genre> genres;

	public Movie(String title, int year, String director) {
		this.title = title;
		this.year = year;
		this.director = director;
		genres = new ArrayList<>();
	}

	public Movie(String title, int year, String director, List<Genre> genres) {
		this.title = title;
		this.year = year;
		this.director = director;
		this.genres = genres;
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

	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public void addGenre(Genre genre) {
		this.genres.add(genre);
	}

	public void clearGenres() {
		this.genres.clear();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Movie{title:").append(getTitle());
		sb.append(", year:").append(getYear());
		sb.append(", director:").append(getDirector());
		sb.append(", genres:").append(getGenres()).append("}");
		return sb.toString();
	}
}
