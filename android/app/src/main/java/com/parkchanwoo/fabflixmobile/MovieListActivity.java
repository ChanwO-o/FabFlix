package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

	private static final int PAGESIZE = 20; // max number of Movie items per page

	private RecyclerView rvMovieList;
	private MovieListAdapter movieListAdapter;
	private LinearLayout llLoadingMovieListLayout;
	private String url = "https://18.209.31.65:8443/cs122b-spring20-team-131/api/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);
		goFullScreen();

		rvMovieList = findViewById(R.id.rvMovieList);
		llLoadingMovieListLayout = findViewById(R.id.llLoadingMovieListLayout);
		movieListAdapter = new MovieListAdapter();
		movieListAdapter.setOnItemClickListener(new MovieListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Movie movie) {
				Intent intent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
				intent.putExtra("movie", movie);
				startActivity(intent);
			}
		});

		final RequestQueue queue = NetworkManager.sharedManager(this).queue;
		final StringRequest movieListRequest = new StringRequest(Request.Method.GET, url + "movies", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
//				Log.d("fabflixandroid", "MovieList response: " + response);
				try {
					JSONArray jsonArray = new JSONArray(response);
					ArrayList<Movie> movies = parseMovies(jsonArray);
					refreshMovieList(movies);
					llLoadingMovieListLayout.setVisibility(View.INVISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("fabflixandroid", "error: " + error.toString()); // error
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Post request form data
				final Map<String, String> params = new HashMap<>();
				params.put("title", "term");
				params.put("year", "");
				params.put("director", "");
				params.put("star", "");
				params.put("pn", String.valueOf(PAGESIZE));
				params.put("pg", String.valueOf(1));
				return params;
			}
		};
		Log.d("fabflixandroid", "movieListRequest: " + movieListRequest);
		movieListRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(movieListRequest);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		goFullScreen();
	}

	private ArrayList<Movie> parseMovies(JSONArray jsonArray) throws JSONException {
		ArrayList<Movie> result = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Log.d("fabflixandroid", "jsonObject: " + jsonObject);

			String movieId = jsonObject.getString("movie_id");
			String starIds = jsonObject.getString("star_id");
			String movieTitle = jsonObject.getString("movie_title");
			short movieYear = (short) jsonObject.getDouble("movie_year");
			String movieDirector = jsonObject.getString("movie_director");
			short movieRating = (short) jsonObject.getDouble("movie_rating");
			String genreNames = jsonObject.getString("movie_genres");
			String starNames = jsonObject.getString("movie_stars");

			Movie movie = new Movie(movieId, movieTitle, movieDirector, movieYear, starNames, genreNames);
			result.add(movie);
		}
		return result;
	}

	private void refreshMovieList(List<Movie> movies) {
		movieListAdapter.setMovies(movies);
		rvMovieList.setLayoutManager(new LinearLayoutManager(this));
//		rvMovieList.setLayoutManager(new GridLayoutManager(this, MovieListAdapter.NUM_COLUMNS));
//		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
//		rvMovieList.addItemDecoration(itemDecoration);
		rvMovieList.setHasFixedSize(true);
		rvMovieList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		rvMovieList.setAdapter(movieListAdapter);
	}

	private void goFullScreen() {
		int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiOptions);
	}
}
