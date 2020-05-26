package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import java.util.Locale;

public class SingleMovieActivity extends AppCompatActivity {

	private TextView tvSingleMovieTitle, tvSingleMovieYear, tvSingleMovieDirector, tvSingleMovieGenres, tvSingleMovieStars;

	private static final String URL = "https://18.209.31.65:8443/cs122b-spring20-team-131/api/single-movie?title=%s";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_movie);
		goFullScreen();

		tvSingleMovieTitle = findViewById(R.id.tvSingleMovieTitle);
		tvSingleMovieYear = findViewById(R.id.tvSingleMovieYear);
		tvSingleMovieDirector = findViewById(R.id.tvSingleMovieDirector);
		tvSingleMovieGenres = findViewById(R.id.tvSingleMovieGenres);
		tvSingleMovieStars = findViewById(R.id.tvSingleMovieStars);

		Intent intent = getIntent();
		String movieTitle = intent.getStringExtra("movieTitle");
		Movie movie = (Movie) intent.getSerializableExtra("movie");

		if (movieTitle != null) { // from MainPageActivity full text suggestion
//			Toast.makeText(this, "loadMovieDetailsByTitle() movieTitle: " + movieTitle, Toast.LENGTH_SHORT).show();
			loadMovieDetailsByTitle(movieTitle);
		}
		else if (movie != null) { // from MovieListActivity list item click
//			Toast.makeText(this, "loadMovieDetailsByMovie() movie: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
			loadMovieDetailsByMovie(movie);
		}
	}

	private void loadMovieDetailsByTitle(String movieTitle) {
		String finalUrl = String.format(Locale.getDefault(), URL, movieTitle);
		Log.d("fabflixandroid", "finalUrl: " + finalUrl);

		final RequestQueue queue = NetworkManager.sharedManager(SingleMovieActivity.this).queue;
		final StringRequest singleMovieRequest = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("fabflixandroid", "singleMovieRequest response: " + response);
				try {
					JSONArray jsonArray = new JSONArray(response);
					for (int i = 0; i < jsonArray.length(); ++i) {
						JSONObject singleMovieJson = (JSONObject) jsonArray.get(i);
						tvSingleMovieTitle.setText(singleMovieJson.getString("movie_title"));
						tvSingleMovieYear.setText(singleMovieJson.getString("movie_year"));
						tvSingleMovieDirector.setText(singleMovieJson.getString("movie_director"));
						tvSingleMovieGenres.setText(singleMovieJson.getString("movie_genres"));
						tvSingleMovieStars.setText(singleMovieJson.getString("movie_stars"));
						break; // use first single movie result
					}

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
		singleMovieRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(singleMovieRequest);
	}

	private void loadMovieDetailsByMovie(Movie movie) {
		tvSingleMovieTitle.setText(movie.getTitle());
		tvSingleMovieYear.setText(String.valueOf(movie.getYear()));
		tvSingleMovieDirector.setText(movie.getDirector());
		tvSingleMovieGenres.setText(movie.getGenreNamesAsString(10));
		tvSingleMovieStars.setText(movie.getStarNamesAsString(10));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		goFullScreen();
	}

	private void goFullScreen() {
		int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiOptions);
	}
}
