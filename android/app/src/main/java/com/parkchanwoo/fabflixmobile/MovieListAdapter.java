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
	private static final int ITEMS_PER_PAGE = 20;
	private int pageNum = 1;

	public static final int NUM_COLUMNS = 5;

	public MovieListAdapter() {
		movies = new ArrayList<>();
	}

	public MovieListAdapter(List<Movie> movies) {
		this.movies = movies;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
		return new ViewHolder(rowItem);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Movie movie = movies.get(position);
		holder.tvMovieListRowTitle.setText(movie.getTitle());
		holder.tvMovieListRowYear.setText(String.valueOf(movie.getYear()));
		holder.tvMovieListRowDirector.setText(movie.getDirector());
		holder.tvMovieListRowGenres.setText(movie.getGenreNamesAsString(3));
		holder.tvMovieListRowStars.setText(movie.getStarNamesAsString(3));
	}

	@Override
	public int getItemCount() {
		if (movies == null) return 0;
		return movies.size();
	}

	public List<Movie> getMovies() {
		return movies;
	}

	/**
	 * Extract all Movie objects that should be on current page, set as adapter's dataset
	 */
	public void setMovies(List<Movie> movies) {
		// 0 ~ 19: (pageNum - 1) * ipp <= i < pageNum * ipp
		// 20 ~ 39: (pageNum - 1) * ipp < i < pageNum * ipp

		ArrayList<Movie> currentPageMovies = new ArrayList<>();
		Log.d("fabflixandroid", "bounds: " + (pageNum - 1) * ITEMS_PER_PAGE + " ~ " + (pageNum * ITEMS_PER_PAGE));
		for (int i = 0; i < movies.size(); ++i) {
			if ((pageNum - 1) * ITEMS_PER_PAGE <= i && i < (pageNum * ITEMS_PER_PAGE))
				currentPageMovies.add(movies.get(i));
		}
		this.movies = currentPageMovies;
		notifyDataSetChanged();
	}

	public Movie getMovieAt(int position) {
		return movies.get(position);
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void nextPage() {
		this.pageNum++;
	}

	public void previousPage() {
		this.pageNum--;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private TextView tvMovieListRowTitle, tvMovieListRowYear, tvMovieListRowDirector, tvMovieListRowGenres, tvMovieListRowStars;

		public ViewHolder(View itemView) {
			super(itemView);
			tvMovieListRowTitle = itemView.findViewById(R.id.tvMovieListRowTitle);
			tvMovieListRowYear = itemView.findViewById(R.id.tvMovieListRowYear);
			tvMovieListRowDirector = itemView.findViewById(R.id.tvMovieListRowDirector);
			tvMovieListRowGenres = itemView.findViewById(R.id.tvMovieListRowGenres);
			tvMovieListRowStars = itemView.findViewById(R.id.tvMovieListRowStars);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = getAdapterPosition();
					if (listener != null && position != RecyclerView.NO_POSITION)
						listener.onItemClick(movies.get(position));
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
