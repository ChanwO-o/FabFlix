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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

public class MovieListActivity extends AppCompatActivity {

	private static final int PAGESIZE = 20; // max number of Movie items per page

	private RecyclerView rvMovieList;
	private MovieListAdapter movieListAdapter;
	private LinearLayout llLoadingMovieListLayout;
	private Button bMovieListPrevious, bMovieListNext;

	private String searchTerm;

//	private static final String URL = "https://18.209.31.65:8443/cs122b-spring20-team-131/api/movies?title=%s&year=&director=&star=";
	private static final String URL = "https://18.209.31.65:8443/cs122b-spring20-team-131/api/movies?fulltext=%s";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);
		goFullScreen();

		rvMovieList = findViewById(R.id.rvMovieList);
		llLoadingMovieListLayout = findViewById(R.id.llLoadingMovieListLayout);
		bMovieListPrevious = findViewById(R.id.bMovieListPrevious);
		bMovieListNext = findViewById(R.id.bMovieListNext);

		bMovieListPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (movieListAdapter.getPageNum() == 1) {
					Toast.makeText(MovieListActivity.this, "At first page", Toast.LENGTH_SHORT).show();
					return; // min pageNum is 1
				}
				movieListAdapter.previousPage();
				llLoadingMovieListLayout.setVisibility(View.VISIBLE);
				makeMovieSearchRequest(searchTerm);
			}
		});

		bMovieListNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				movieListAdapter.nextPage();
				llLoadingMovieListLayout.setVisibility(View.VISIBLE);
				makeMovieSearchRequest(searchTerm);
			}
		});

		movieListAdapter = new MovieListAdapter();
		movieListAdapter.setOnItemClickListener(new MovieListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Movie movie) {
				Intent intent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
				intent.putExtra("movie", movie);
				startActivity(intent);
			}
		});

		Intent intent = getIntent(); // get search term from main page
		searchTerm = intent.getStringExtra("search");
		makeMovieSearchRequest(searchTerm);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		goFullScreen();
	}

	private void makeMovieSearchRequest(String title) {
		String finalUrl = String.format(Locale.getDefault(), URL, title);
		Log.d("fabflixandroid", "finalUrl: " + finalUrl);
		final RequestQueue queue = NetworkManager.sharedManager(this).queue;
		final StringRequest movieListRequest = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
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
					Log.d("fabflixandroid", "error: " + e.getMessage());
				}
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("fabflixandroid", "error: " + error.toString()); // error
					}
				});
		Log.d("fabflixandroid", "movieListRequest: " + movieListRequest);
		movieListRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(movieListRequest);
	}

	private ArrayList<Movie> parseMovies(JSONArray jsonArray) throws JSONException {
		ArrayList<Movie> result = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//			Log.d("fabflixandroid", "jsonObject: " + jsonObject);

			String movieId = jsonObject.getString("movie_id");
			String starIds = jsonObject.getString("star_id");
			String movieTitle = jsonObject.getString("movie_title");
			short movieYear = (short) jsonObject.getDouble("movie_year");
			String movieDirector = jsonObject.getString("movie_director");
			String movieRating = jsonObject.getString("movie_rating");
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
