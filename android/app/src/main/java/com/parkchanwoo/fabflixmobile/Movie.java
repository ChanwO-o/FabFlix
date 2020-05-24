package com.parkchanwoo.fabflixmobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Movie {
	private String movieId, title, director;
	private short year;
	private short rating;
	private List<String> starIds, starNames, genreNames;

	public Movie(String movieId, String title, String director, short year, String starNames, String genreNames) {
		this.movieId = movieId;
		this.title = title;
		this.director = director;
		this.year = year;
		this.starNames = parseStarNames(starNames);
		this.genreNames = parseGenreNames(genreNames);
	}

	public Movie(String movieId, String title, String director, short year, short rating, String starIds, String starNames, String genreNames) {
		this.movieId = movieId;
		this.title = title;
		this.director = director;
		this.year = year;
		this.rating = rating;
		this.starIds = parseStarIds(starIds);
		this.starNames = parseStarNames(starNames);
		this.genreNames = parseGenreNames(genreNames);
	}

	public String getMovieId() {
		return movieId;
	}

	public String getTitle() {
		return title;
	}

	public String getDirector() {
		return director;
	}

	public short getYear() {
		return year;
	}

	public short getRating() {
		return rating;
	}

	public List<String> getStarIds() {
		return starIds;
	}

	public List<String> getStarNames() {
		return starNames;
	}

	public String getStarNamesAsString(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			if (starNames.size() >= i + 1) { // check if movie has i number of stars
				sb.append(starNames.get(i));
				if (i != count - 1 && starNames.size() >= i + 2)
					sb.append(", ");
			}
		}
		return sb.toString();
	}

	public List<String> getGenreNames() {
		return genreNames;
	}

	public String getGenreNamesAsString(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			if (genreNames.size() >= i + 1) {
				sb.append(genreNames.get(i));
				if (i != count - 1 && genreNames.size() >= i + 2)
					sb.append(", ");
			}
		}
		return sb.toString();
	}

	private List<String> parseStarIds(String starIds) {
		ArrayList<String> result = new ArrayList<>(Arrays.asList(starIds.split(",")));
		return result;
	}

	private List<String> parseStarNames(String starNames) {
		ArrayList<String> result = new ArrayList<>(Arrays.asList(starNames.split(",")));
		return result;
	}

	private List<String> parseGenreNames(String genreNames) {
		ArrayList<String> result = new ArrayList<>(Arrays.asList(genreNames.split(",")));
		return result;
	}
}
