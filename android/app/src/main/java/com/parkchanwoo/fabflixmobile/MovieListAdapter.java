package com.parkchanwoo.fabflixmobile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
	private List<Movie> movies;
	private OnItemClickListener listener;

	public MovieListAdapter() {
		movies = new ArrayList<>();
	}

	public MovieListAdapter(List<Movie> movies) {
		this.movies = movies;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d("MovieListAdapter.onCreateViewHolder()", "yup");
		View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
		return new ViewHolder(rowItem);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Log.d("MovieListAdapter.onBindViewHolder()", "binding position: " + position);
		Movie movie = movies.get(position);
		Log.d("MovieListAdapter.onBindViewHolder()", "name of movie at position: " + movie.getName());
		holder.tvMovieListRowTitle.setText(movie.getName());
	}

	@Override
	public int getItemCount() {
		if (movies == null) return 0;
		return movies.size();
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
		notifyDataSetChanged();
	}

	public Movie getMovieAt(int position) {
		return movies.get(position);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private TextView tvMovieListRowTitle;

		public ViewHolder(View itemView) {
			super(itemView);
			tvMovieListRowTitle = itemView.findViewById(R.id.tvMovieListRowTitle);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// go to movie details
				}
			});
		}
	}

	public interface OnItemClickListener {
		void onItemClick(Movie movie);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}
}
