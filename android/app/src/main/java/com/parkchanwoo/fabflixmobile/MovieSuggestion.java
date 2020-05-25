package com.parkchanwoo.fabflixmobile;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class MovieSuggestion implements SearchSuggestion {

	private String movieId, movieTitle;

	public MovieSuggestion(String movieId, String movieTitle) {
		this.movieId = movieId;
		this.movieTitle = movieTitle;
	}

	public MovieSuggestion(Parcel in) {
		movieId = in.readString();
		movieTitle = in.readString();
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
		dest.writeString(this.movieId);
		dest.writeString(this.movieTitle);
	}
}
