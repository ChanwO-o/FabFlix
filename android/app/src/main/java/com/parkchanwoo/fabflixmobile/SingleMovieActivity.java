package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SingleMovieActivity extends AppCompatActivity {

	private TextView tvSingleMovieTitle, tvSingleMovieYear, tvSingleMovieDirector, tvSingleMovieGenres, tvSingleMovieStars;

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
		Movie movie = (Movie) intent.getSerializableExtra("movie");
		Toast.makeText(this, "movie: " + movie.getTitle() + movie.getYear(), Toast.LENGTH_SHORT).show();

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
