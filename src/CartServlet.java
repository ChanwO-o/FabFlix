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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");

		// If "cartList" is not found on session, means this is a new user, thus we create a new cartList for the user
		if (cartList == null) {
			System.out.println("no cartList found; creating new cartList for user");
			// Add the newly created cartList to session, so that it could be retrieved next time
			session.setAttribute("cartList", new HashMap<String, Integer>());
			cartList = (HashMap<String, Integer>) session.getAttribute("cartList"); // reassign cartList variable to newly created list
		}

		String movieId = request.getParameter("id"); // Get parameter that sent by GET request url
		String remove = request.getParameter("remove");
		String update = request.getParameter("update");
		String quantity = request.getParameter("qty");
		System.out.println("movieId: " + movieId + " remove: " + remove + " update: " + update + " quantity: " + quantity);

		// Case 1: add movie to cart
		// In order to prevent multiple clients, requests from altering cartList at the same time, we lock the cartList while updating
		if (movieId != null && remove == null && update == null) {
			synchronized (cartList) {
				if (cartList.containsKey(movieId)) {
					System.out.println("add to cartList: movie existing, incrementing by 1");
					cartList.put(movieId, cartList.get(movieId) + 1); // increment quantity by 1
				} else {
					System.out.println("add to cartList: new movie, qty set to 1");
					cartList.put(movieId, 1); // new movie added to cart
				}
				session.setAttribute("cartList", cartList); // save updated cartList to user session
				System.out.println("cartList updated: " + cartList);
			}
		}
		// Case 2: remove movie from cart
		else if (movieId != null && remove != null && remove.equals("true") && update == null) {
			System.out.println("removing movie with movieId: " + movieId);
			synchronized (cartList) {
				cartList.remove(movieId);
				session.setAttribute("cartList", cartList); // save updated cartList to user session
				System.out.println("cartList updated: " + cartList);
			}
		}
		// Case 3: update existing movie in cart to quantity
		else if (movieId != null && remove == null && update != null && update.equals("true") && quantity != null) {
			System.out.println("updating movie with movieId: " + movieId + " to quantity: " + quantity);
			synchronized (cartList) {
				int q = Integer.parseInt(quantity);
				if (q <= 0) // remove movie if updated quantity is 0 or less
					cartList.remove(movieId);
				else
					cartList.put(movieId, q);
				session.setAttribute("cartList", cartList); // save updated cartList to user session
				System.out.println("cartList updated: " + cartList);
			}
		}

		if (cartList.isEmpty()) { // if cart is empty, no need to query anything!
			out.write(new JsonArray().toString());
			response.setStatus(200);
			out.close();
			return;
		}

		try {
			Connection dbcon = dataSource.getConnection(); // Get a connection from dataSource
			String query = "select movies.id,movies.title,movies.year,movies.director," +
					"group_concat(distinct genres.name separator ',') as genres,group_concat(distinct stars.name separator ',') as stars,ratings.rating " +
					"from movies, ratings, genres, genres_in_movies, stars, stars_in_movies " +
					"where ratings.movieId=movies.id and movies.id in (";

			// append each movie id in cart to query
			for (int i = 0; i < cartList.size(); ++i) {
				query += "?";
				if (i != cartList.size() - 1)
					query += ",";
			}
			query += ") "; // closing bracket for movieId group

			query += "and movies.id=genres_in_movies.movieId and genres_in_movies.genreId=genres.id " +
					"and movies.id=stars_in_movies.movieId and stars.id=stars_in_movies.starId " +
					"group by movies.id"; // finish the query
			System.out.println("query: " + query);

			PreparedStatement statement = dbcon.prepareStatement(query);
			int entryCounter = 0;
			for (Map.Entry<String, Integer> movieEntry : cartList.entrySet()) {
				entryCounter++;
				statement.setString(entryCounter, movieEntry.getKey());
			}
			ResultSet rs = statement.executeQuery();
			dbcon.commit();

			JsonArray jsonArray = new JsonArray();

			while (rs.next()) {
				String movie_id = rs.getString("id");
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_genres = rs.getString("genres");
				String movie_stars = rs.getString("stars");
				String movie_rating = rs.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movie_id);
				jsonObject.addProperty("movie_title", movie_title);
				jsonObject.addProperty("movie_year", movie_year);
				jsonObject.addProperty("movie_director", movie_director);
				jsonObject.addProperty("movie_genres", movie_genres);
				jsonObject.addProperty("movie_stars", movie_stars);
				jsonObject.addProperty("movie_rating", movie_rating);
				jsonObject.addProperty("movie_price", generateMoviePrice(movie_id));
				jsonObject.addProperty("movie_quantity", cartList.get(movie_id));
				jsonArray.add(jsonObject);
			}
			System.out.println("cartList jsonArray: " + jsonArray);

			// write JSON string to output
			out.write(jsonArray.toString());
			// set response status to 200 (OK)
			response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}

	private double generateMoviePrice(String movie_id) {
		return 15.00;
	}
}
