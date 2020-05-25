package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

public class MainPageActivity extends AppCompatActivity {

	private FloatingSearchView fsvMovieSearchView;
	private Button bMainPageSearch;

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

				//pass them on to the search view
//				fsvMovieSearchView.swapSuggestions(newSuggestions);
			}
		});

		bMainPageSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
