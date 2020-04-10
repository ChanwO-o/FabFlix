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
import java.sql.*;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		System.out.println("id: " + id);

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			Statement statement = dbcon.createStatement();

			// Construct a query with parameter represented by "?"
			String query = "select movies.title, movies.year, movies.director, group_concat(distinct genres.name) as genres, group_concat(distinct stars.name) as stars,ratings.rating " +
					"from movies, genres, stars,stars_in_movies,genres_in_movies,ratings " +
					"where movies.id = genres_in_movies.movieId " +
					"and genres_in_movies.genreId=genres.id " +
					"and stars_in_movies.movieId=movies.id " +
					"and stars_in_movies.starId=stars.id " +
					"and ratings.movieId=movies.id " +
					"and movies.id='tt0378947';";

			ResultSet rs = statement.executeQuery(query);

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next())
			{
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_genres = rs.getString("genres");
				String movie_stars = rs.getString("stars");
				String movie_rating = rs.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_title", movie_title);
				jsonObject.addProperty("movie_year", movie_year);
				jsonObject.addProperty("movie_director", movie_director);
				jsonObject.addProperty("movie_genres", movie_genres);
				jsonObject.addProperty("movie_stars", movie_stars);
				jsonObject.addProperty("movie_rating", movie_rating);
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
