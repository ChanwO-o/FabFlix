package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

	private RecyclerView rvMovieList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);

		rvMovieList = findViewById(R.id.rvMovieList);

		final ArrayList<Movie> movies = new ArrayList<>();
		movies.add(new Movie("The Terminal", (short) 2004));
		movies.add(new Movie("The Final Season", (short) 2007));

		MovieListAdapter movieListAdapter = new MovieListAdapter(movies);
		Log.d("MovieListActivity.onCreate()", "adapter size: " + movieListAdapter.getItemCount());

		rvMovieList.setLayoutManager(new LinearLayoutManager(this));
		rvMovieList.setHasFixedSize(true);
		rvMovieList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		rvMovieList.setAdapter(movieListAdapter);
	}
}
