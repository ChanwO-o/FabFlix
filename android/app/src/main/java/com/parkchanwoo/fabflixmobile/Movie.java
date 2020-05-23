package com.parkchanwoo.fabflixmobile;

public class Movie {
	private String title, director;
	private short year;

	public Movie(String title, short year, String director) {
		this.title = title;
		this.year = year;
		this.director = director;
	}

	public String getTitle() {
		return title;
	}

	public short getYear() {
		return year;
	}

	public String getDirector() {
		return director;
	}
}
