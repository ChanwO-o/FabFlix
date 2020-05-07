import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameters from url request, replacing '+' (url param behavior) to spaces
		String title = request.getParameter("title");
		if (title != null)
			title = title.replace('+', ' ');
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		if (director != null)
			director = director.replace('+', ' ');
		String star = request.getParameter("star");
		if (star != null)
			star = star.replace('+', ' ');
		String genres = request.getParameter("genres");
		String title_start = request.getParameter("title_start");
		String first_sort = request.getParameter("first_sortby");
		String second_sort = request.getParameter("second_sortby");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		if (first_sort == null || first_sort == "") {
			if (genres == null || genres == "") //check browse by genres
			{
				if ((title == null || title.equals("")) && (year == null || year.equals("")) &&
						(director == null || director.equals("")) && (star == null || star.equals(""))) {
					if ((title_start == null) || title_start == "") {
						try {
							// Get a connection from dataSource
							Connection dbcon = dataSource.getConnection();

							// Declare our statement
							Statement statement = dbcon.createStatement();

							String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
									", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
									"group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId " +
									"left join ratings on ratings.movieId=movies.id inner join genres on genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
									"inner join stars on stars_in_movies.starId=stars.id group by movies.id order by rating desc limit 20";
							// Perform the query
							ResultSet rs = statement.executeQuery(query);
							JsonArray jsonArray = new JsonArray();

							// Iterate through each row of rs
							while (rs.next()) {
								String movie_id = rs.getString("id");
								String star_id = rs.getString("star_id");
								String movie_title = rs.getString("title");
								String movie_year = rs.getString("year");
								String movie_director = rs.getString("director");
								String movie_rating = rs.getString("rating");
								String movie_genres = rs.getString("genres");
								String movie_stars = rs.getString("stars");

								// Create a JsonObject based on the data we retrieve from rs
								JsonObject jsonObject = new JsonObject();
								jsonObject.addProperty("movie_id", movie_id);
								jsonObject.addProperty("star_id", star_id);
								jsonObject.addProperty("movie_title", movie_title);
								jsonObject.addProperty("movie_year", movie_year);
								jsonObject.addProperty("movie_director", movie_director);
								jsonObject.addProperty("movie_rating", movie_rating);
								jsonObject.addProperty("movie_genres", movie_genres);
								jsonObject.addProperty("movie_stars", movie_stars);
								jsonArray.add(jsonObject);
							}
							// write JSON string to output
							out.write(jsonArray.toString());
							// set response status to 200 (OK)
							response.setStatus(200);

							rs.close();
							statement.close();
							dbcon.close();
						} catch (Exception e) {
						    e.printStackTrace();
							// write error message JSON object to output
							JsonObject jsonObject = new JsonObject();
							jsonObject.addProperty("errorMessage", e.getMessage());
							out.write(jsonObject.toString());
							// set reponse status to 500 (Internal Server Error)
							response.setStatus(500);
						}
						out.close();
					}
					else {
						try {
							// Get a connection from dataSource
							Connection dbcon = dataSource.getConnection();
							dbcon.setAutoCommit(false);

							String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
									", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
									"group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId " +
									"left join ratings on ratings.movieId=movies.id inner join genres on genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
									"inner join stars on stars_in_movies.starId=stars.id ";

							if (!title_start.isEmpty()) {
								if (title_start.contains("*")) {
									query += " and movies.title not REGEXP '^[0-9a-z]'";
								} else
									query += " and movies.title like ?";
							}
							query += " group by movies.id ";

							// Declare our PreparedStatement
							PreparedStatement statement = dbcon.prepareStatement(query);
							if (statement.getParameterMetaData().getParameterCount() > 0) // set param only if at least one ? exists
								statement.setString(1, title_start + "%");

							// Perform the query
							ResultSet rs = statement.executeQuery();
							dbcon.commit();

							JsonArray jsonArray = new JsonArray();

							// Iterate through each row of rs
							while (rs.next()) {
								String movie_id = rs.getString("id");
								String star_id = rs.getString("star_id");
								String movie_title = rs.getString("title");
								String movie_year = rs.getString("year");
								String movie_director = rs.getString("director");
								String movie_rating = rs.getString("rating");
								String movie_genres = rs.getString("genres");
								String movie_stars = rs.getString("stars");

								// Create a JsonObject based on the data we retrieve from rs
								JsonObject jsonObject = new JsonObject();
								jsonObject.addProperty("movie_id", movie_id);
								jsonObject.addProperty("star_id", star_id);
								jsonObject.addProperty("movie_title", movie_title);
								jsonObject.addProperty("movie_year", movie_year);
								jsonObject.addProperty("movie_director", movie_director);
								jsonObject.addProperty("movie_rating", movie_rating);
								jsonObject.addProperty("movie_genres", movie_genres);
								jsonObject.addProperty("movie_stars", movie_stars);
								jsonArray.add(jsonObject);
							}
							// write JSON string to output
							out.write(jsonArray.toString());
							// set response status to 200 (OK)
							response.setStatus(200);

							rs.close();
							statement.close();
							dbcon.close();
						} catch (Exception e) {
						    e.printStackTrace();
							// write error message JSON object to output
							JsonObject jsonObject = new JsonObject();
							jsonObject.addProperty("errorMessage", e.getMessage());
							out.write(jsonObject.toString());
							// set reponse status to 500 (Internal Server Error)
							response.setStatus(500);
						}
						out.close();
					}
				}
				else {
					try {
						// Get a connection from dataSource
						Connection dbcon = dataSource.getConnection();
						dbcon.setAutoCommit(false);

						String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
								", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
								"group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId" +
								" left join ratings on ratings.movieId=movies.id inner join genres on " +
								"genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
								"inner join stars on stars_in_movies.starId=stars.id ";

						Map<Integer, String> paramMap = new HashMap<>();
						if (!title.isEmpty()) {
							paramMap.put(paramMap.size() + 1, "%" + title + "%");
							query += " and movies.title like ?";
						}
						if (!year.isEmpty()) {
							paramMap.put(paramMap.size() + 1, year);
							query += " and movies.year = ?";
						}
						if (!director.isEmpty()) {
							paramMap.put(paramMap.size() + 1, "%" + director + "%");
							query += " and movies.director like ?";
						}
						if (!star.isEmpty()) {
							paramMap.put(paramMap.size() + 1, "%" + star + "%");
							query += " and stars.name like ?";
						}
						query += " group by movies.id order by rating desc";
						System.out.println(query);

						// Declare our PreparedStatement
						PreparedStatement statement = dbcon.prepareStatement(query);
						// apply params
						for (Map.Entry<Integer, String> entry : paramMap.entrySet()) {
							statement.setString(entry.getKey(), entry.getValue());
						}

						// Perform the query
						ResultSet rs = statement.executeQuery();
						dbcon.commit();

						JsonArray jsonArray = new JsonArray();

						// Iterate through each row of rs
						while (rs.next()) {
							String movie_id = rs.getString("id");
							String star_id = rs.getString("star_id");
							String movie_title = rs.getString("title");
							String movie_year = rs.getString("year");
							String movie_director = rs.getString("director");
							String movie_rating = rs.getString("rating");
							String movie_genres = rs.getString("genres");
							String movie_stars = rs.getString("stars");

							// Create a JsonObject based on the data we retrieve from rs
							JsonObject jsonObject = new JsonObject();
							jsonObject.addProperty("movie_id", movie_id);
							jsonObject.addProperty("star_id", star_id);
							jsonObject.addProperty("movie_title", movie_title);
							jsonObject.addProperty("movie_year", movie_year);
							jsonObject.addProperty("movie_director", movie_director);
							jsonObject.addProperty("movie_rating", movie_rating);
							jsonObject.addProperty("movie_genres", movie_genres);
							jsonObject.addProperty("movie_stars", movie_stars);
							jsonObject.addProperty("pn_temp", "10");
							jsonObject.addProperty("pg_temp", "1");
							jsonArray.add(jsonObject);
						}
						// write JSON string to output
						out.write(jsonArray.toString());
						// set response status to 200 (OK)
						response.setStatus(200);

						rs.close();
						statement.close();
						dbcon.close();
					} catch (Exception e) {
                        e.printStackTrace();
						// write error message JSON object to output
						JsonObject jsonObject = new JsonObject();
						jsonObject.addProperty("errorMessage", e.getMessage());
						out.write(jsonObject.toString());
						// set reponse status to 500 (Internal Server Error)
						response.setStatus(500);
					}
					out.close();
				}
			}
			else { // browse by genres query on
				try {
					// Get a connection from dataSource
					Connection dbcon = dataSource.getConnection();
					// Declare our statement
					Statement statement = dbcon.createStatement();

					String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
							", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as g, " +
							"group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId" +
							" left join ratings on ratings.movieId=movies.id inner join genres on " +
							"genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
							"inner join stars on stars_in_movies.starId=stars.id group by movies.id";

					JsonArray jsonArray = new JsonArray();

                    // Perform the query
					ResultSet rs = statement.executeQuery(query);
					// Iterate through each row of rs
					while (rs.next()) {
						String movie_id = rs.getString("id");
						String star_id = rs.getString("star_id");
						String movie_title = rs.getString("title");
						String movie_year = rs.getString("year");
						String movie_director = rs.getString("director");
						String movie_rating = rs.getString("rating");
						String movie_genres = rs.getString("g");
						String movie_stars = rs.getString("stars");

						// Create a JsonObject based on the data we retrieve from rs
						// System.out.println("movie_genres:" + movie_genres);
						//System.out.println("Genres that query gives:" +genres);

						if (movie_genres.contains(genres)) {
							JsonObject jsonObject = new JsonObject();
							jsonObject.addProperty("movie_id", movie_id);
							jsonObject.addProperty("star_id", star_id);
							jsonObject.addProperty("movie_title", movie_title);
							jsonObject.addProperty("movie_year", movie_year);
							jsonObject.addProperty("movie_director", movie_director);
							jsonObject.addProperty("movie_rating", movie_rating);
							jsonObject.addProperty("movie_genres", movie_genres);
							jsonObject.addProperty("movie_stars", movie_stars);
							jsonArray.add(jsonObject);
						}
					}
					// write JSON string to output
					out.write(jsonArray.toString());
					// set response status to 200 (OK)
					response.setStatus(200);

					rs.close();
					statement.close();
					dbcon.close();
				} catch (Exception e) {
                    e.printStackTrace();
					// write error message JSON object to output
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("errorMessage", e.getMessage());
					out.write(jsonObject.toString());

					// set reponse status to 500 (Internal Server Error)
					response.setStatus(500);
				}
				out.close();
			}
		}
		else { // sorting here
			try {
				// Get a connection from dataSource
				Connection dbcon = dataSource.getConnection();
				dbcon.setAutoCommit(false);

				String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
						", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
						"group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId" +
						" left join ratings on ratings.movieId=movies.id inner join genres on " +
						"genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
						"inner join stars on stars_in_movies.starId=stars.id ";

				Map<Integer, String> paramMap = new HashMap<>();
				if (title != null && !title.isEmpty()) {
					paramMap.put(paramMap.size() + 1, "%" + title + "%");
					query += " and movies.title like ?";
				}
				if (year != null && !year.isEmpty()) {
					paramMap.put(paramMap.size() + 1, year);
					query += " and movies.year = ?";
				}
				if (director != null && !director.isEmpty()) {
					paramMap.put(paramMap.size() + 1, "%" + director + "%");
					query += " and movies.director like ?";
				}
				if (star != null && !star.isEmpty()) {
					paramMap.put(paramMap.size() + 1, "%" + star + "%");
					query += " and stars.name like ?";
				}
				if (title_start != null && !title_start.isEmpty()) {
					if (title_start.contains("*"))
						query += " and movies.title not REGEXP '^[0-9a-z]'";
					else {
						paramMap.put(paramMap.size() + 1, title_start + "%");
						query += " and movies.title like ?";
					}
				}
				if (genres != null && !genres.isEmpty()) {
					paramMap.put(paramMap.size() + 1, genres);
					query += " and genres.name = ?";
				}
				query += " group by movies.title ";
				if (first_sort.equals("title_asc")) {
					if (second_sort.equals("title_asc")) {
						query += "order by movies.title";
					} else if (second_sort.equals("title_dsc")) {
						query += "order by movies.title desc";
					} else if (second_sort.equals("rating_asc")) {
						query += "order by movies.title, rating ASC ";
					} else if (second_sort.equals("rating_dsc")) {
						query += "order by movies.title, rating DESC";
					} else {
						query += "order by movies.title";
					}
				} else if (first_sort.equals("title_dsc")) {
					if (second_sort.equals("title_asc")) {
						query += "order by movies.title";
					} else if (second_sort.equals("title_dsc")) {
						query += "order by movies.title desc";
					} else if (second_sort.equals("rating_asc")) {
						query += "order by movies.title desc, rating ASC";
					} else if (second_sort.equals("rating_dsc")) {
						query += "order by movies.title desc, rating desc";
					} else {
						query += "order by movies.title desc";
					}
				} else if (first_sort.equals("rating_asc")) {
					if (second_sort.equals("title_asc")) {
						query += "order by rating ASC, movies.title ASC";
					} else if (second_sort.equals("title_dsc")) {
						query += "order by rating ASC, movies.title desc";
					} else if (second_sort.equals("rating_asc")) {
						query += "order by rating ASC";
					} else if (second_sort.equals("rating_dsc")) {
						query += "order by rating desc";
					} else {
						query += "order by rating ASC";
					}
				} else {
					if (second_sort.equals("title_asc")) {
						query += "order by rating desc, movies.title ASC";
					} else if (second_sort.equals("title_dsc")) {
						query += "order by rating desc, movies.title desc";
					} else if (second_sort.equals("rating_asc")) {
						query += "order by rating ASC";
					} else if (second_sort.equals("rating_dsc")) {
						query += "order by rating desc";
					} else {
						query += "order by rating desc";
					}
				}

				// Declare our PreparedStatement
				PreparedStatement statement = dbcon.prepareStatement(query);
				// apply params
				for (Map.Entry<Integer, String> entry : paramMap.entrySet()) {
					statement.setString(entry.getKey(), entry.getValue());
				}

				ResultSet rs = statement.executeQuery();
				dbcon.commit();

				JsonArray jsonArray = new JsonArray();

				// Iterate through each row of rs
				while (rs.next()) {
					String movie_id = rs.getString("id");
					String star_id = rs.getString("star_id");
					String movie_title = rs.getString("title");
					String movie_year = rs.getString("year");
					String movie_director = rs.getString("director");
					String movie_rating = rs.getString("rating");
					String movie_genres = rs.getString("genres");
					String movie_stars = rs.getString("stars");

					// Create a JsonObject based on the data we retrieve from rs
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("movie_id", movie_id);
					jsonObject.addProperty("star_id", star_id);
					jsonObject.addProperty("movie_title", movie_title);
					jsonObject.addProperty("movie_year", movie_year);
					jsonObject.addProperty("movie_director", movie_director);
					jsonObject.addProperty("movie_rating", movie_rating);
					jsonObject.addProperty("movie_genres", movie_genres);
					jsonObject.addProperty("movie_stars", movie_stars);
					jsonArray.add(jsonObject);
				}
				// write JSON string to output
				out.write(jsonArray.toString());
				// set response status to 200 (OK)
				response.setStatus(200);

				rs.close();
				statement.close();
				dbcon.close();
			} catch (Exception e) {
			    e.printStackTrace();
				// write error message JSON object to output
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("errorMessage", e.getMessage());
				out.write(jsonObject.toString());
				// set reponse status to 500 (Internal Server Error)
				response.setStatus(500);
			}
		}
	}
}
