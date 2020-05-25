package com.parkchanwoo.fabflixmobile;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class MovieSuggestion implements SearchSuggestion {

	private String movieEntryId, movieTitle;

	public MovieSuggestion(String movieEntryId, String movieTitle) {
		this.movieEntryId = movieEntryId;
		this.movieTitle = movieTitle;
	}

	public MovieSuggestion(Parcel in) {
		movieEntryId = in.readString();
		movieTitle = in.readString();
	}

	public String getMovieEntryId() {
		return movieEntryId;
	}

	public void setMovieEntryId(String movieEntryId) {
		this.movieEntryId = movieEntryId;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}

	public static final Parcelable.Creator<MovieSuggestion> CREATOR
			= new Parcelable.Creator<MovieSuggestion>() {
		public MovieSuggestion createFromParcel(Parcel in) {
			return new MovieSuggestion(in);
		}

		public MovieSuggestion[] newArray(int size) {
			return new MovieSuggestion[size];
		}
	};

	@Override
	public String getBody() {
		return movieTitle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.movieEntryId);
		dest.writeString(this.movieTitle);
	}
}
