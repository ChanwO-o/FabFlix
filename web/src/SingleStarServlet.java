import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
//        System.out.println("id: " + id);

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);

			String query = "SELECT stars.name, stars.birthYear, group_concat(movies.id order by movies.id) as id,group_concat(movies.title order by movies.id) as movie_lists FROM movies, stars_in_movies, stars " +
					"where stars.id = ? " +
					"and movies.id=stars_in_movies.movieId and stars.id=stars_in_movies.starId group by stars.id";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);

			ResultSet rs = statement.executeQuery();
			dbcon.commit();

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {
				String star_name = rs.getString("name");
				String star_dob = rs.getString("birthYear");
				String star_movies = rs.getString("movie_lists");
				String movie_id = rs.getString("id");

				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("star_name", star_name);
				jsonObject.addProperty("star_dob", star_dob);
				jsonObject.addProperty("star_movies", star_movies);
				jsonObject.addProperty("movie_id", movie_id);
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
