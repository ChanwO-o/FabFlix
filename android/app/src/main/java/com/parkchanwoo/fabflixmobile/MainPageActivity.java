package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainPageActivity extends AppCompatActivity {

	private FloatingSearchView fsvMovieSearchView;
	private Button bMainPageSearch;

	private static final String URL = "https://18.209.31.65:8443/cs122b-spring20-team-131/fulltext?query=%s";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		goFullScreen();

		fsvMovieSearchView = findViewById(R.id.fsvMainPage);
		bMainPageSearch = findViewById(R.id.bMainPageSearch);

		fsvMovieSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
			@Override
			public void onSearchTextChanged(String oldQuery, final String newQuery) {
				//get suggestions based on newQuery
				if (newQuery == null || newQuery.length() < 3 || newQuery.trim().isEmpty())
					return; // at least 3 characters required for suggestions

				String finalUrl = String.format(Locale.getDefault(), URL, newQuery);
				Log.d("fabflixandroid", "finalUrl: " + finalUrl);
				final RequestQueue queue = NetworkManager.sharedManager(MainPageActivity.this).queue;
				final StringRequest fulltextSuggestionsRequest = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
				Log.d("fabflixandroid", "fulltext response: " + response);
						try {
							JSONArray jsonArray = new JSONArray(response);
							ArrayList<SearchSuggestion> newSuggestions = parseSuggestions(jsonArray);
							//pass them on to the search view
							fsvMovieSearchView.swapSuggestions(newSuggestions);
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
				fulltextSuggestionsRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
				queue.add(fulltextSuggestionsRequest);
			}
		});

		fsvMovieSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
			@Override
			public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
				MovieSuggestion movieSuggestion = (MovieSuggestion) searchSuggestion;
//				Toast.makeText(MainPageActivity.this, "suggestion clicked: " + movieSuggestion.getMovieEntryId() + " " + movieSuggestion.getMovieTitle(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainPageActivity.this, SingleMovieActivity.class);
//				intent.putExtra("movieId", movieSuggestion.getMovieEntryId());
				intent.putExtra("movieTitle", movieSuggestion.getMovieTitle());
				startActivity(intent);
			}

			@Override
			public void onSearchAction(String query) {
//				Toast.makeText(MainPageActivity.this, "query: " + query, Toast.LENGTH_SHORT).show();
				hideKeyboard();
				String searchTerm = fsvMovieSearchView.getQuery();
				if (searchTerm == null || searchTerm.isEmpty()) {
					Toast.makeText(MainPageActivity.this, "Search field is empty", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(MainPageActivity.this, MovieListActivity.class);
				intent.putExtra("search", searchTerm);
				startActivity(intent);
			}
		});

		bMainPageSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				String searchTerm = fsvMovieSearchView.getQuery();
				if (searchTerm == null || searchTerm.isEmpty()) {
					Toast.makeText(MainPageActivity.this, "Search field is empty", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(MainPageActivity.this, MovieListActivity.class);
				intent.putExtra("search", searchTerm);
				startActivity(intent);
			}
		});
	}

	private ArrayList<SearchSuggestion> parseSuggestions(JSONArray jsonArray) throws JSONException {
		ArrayList<SearchSuggestion> result = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Log.d("fabflixandroid", "jsonObject: " + jsonObject);

			String movieId = jsonObject.getJSONObject("data").getString("movieID");
			String movieTitle = jsonObject.getString("value");

			MovieSuggestion suggestion = new MovieSuggestion(movieId, movieTitle);
			result.add(suggestion);
		}
		return result;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		goFullScreen();
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(this);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void goFullScreen() {
		int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiOptions);
	}
}
