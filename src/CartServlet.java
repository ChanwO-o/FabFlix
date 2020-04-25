import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Declaring a WebServlet called ItemsServlet, which maps to url "/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Get a instance of current session on the request
		HttpSession session = request.getSession();

		PrintWriter out = response.getWriter();
		// Retrieve data named "cartList" from session
		ArrayList<String> cartList = (ArrayList<String>) session.getAttribute("cartList");

		// If "cartList" is not found on session, means this is a new user, thus we create a new cartList for the user
		if (cartList == null) {
			System.out.println("no cartList found; creating new cartList for user");
			// Add the newly created ArrayList to session, so that it could be retrieved next time
			session.setAttribute("cartList", new ArrayList<String>());
			cartList = (ArrayList<String>) session.getAttribute("cartList"); // reassign cartList variable to newly created list
		}

		String newMovieId = request.getParameter("id"); // Get parameter that sent by GET request url
		System.out.println("newMovieId: " + newMovieId);

		// In order to prevent multiple clients, requests from altering cartList at the same time, we
		// lock the ArrayList while updating
		if (newMovieId != null) {
			synchronized (cartList) {
				cartList.add(newMovieId); // Add the new item to the cartList
				session.setAttribute("cartList", cartList); // save updated cartList to user session
			}
		}
		System.out.println("cartList: " + cartList);

		try {
			Connection dbcon = dataSource.getConnection(); // Get a connection from dataSource
			Statement statement = dbcon.createStatement(); // Declare our statement
			String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating from movies, ratings " +
					"where ratings.movieId=movies.id and movies.id in (";

			// append each movie id in cart to query
			for (int i = 0; i < cartList.size(); ++i) {
				query += "'" + cartList.get(i) + "'";
				if (i != cartList.size() - 1)
					query += ",";
			}
			query += ");";

			System.out.println("query: " + query);

			ResultSet rs = statement.executeQuery(query);
			JsonArray jsonArray = new JsonArray();

			while (rs.next()) {
				String movie_id = rs.getString("id");
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_rating = rs.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movie_id);
				jsonObject.addProperty("movie_title", movie_title);
				jsonObject.addProperty("movie_year", movie_year);
				jsonObject.addProperty("movie_director", movie_director);
				jsonObject.addProperty("movie_rating", movie_rating);
				jsonArray.add(jsonObject);
			}
			System.out.println("cartList jsonArray: " + jsonArray);

			// write JSON string to output
			out.write(jsonArray.toString());
			// set response status to 200 (OK)
			response.setStatus(200);
			System.out.println("cart jsonArray sent to frontend");

			rs.close();
			statement.close();
			dbcon.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}
}
