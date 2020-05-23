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
import java.util.ArrayList;
import java.util.Collections;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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
		String id="";
		String title = request.getParameter("title");
		System.out.println("title = " + title);
		if(title!=null && !title.isEmpty())
		{
			try {
				Connection dbcon = dataSource.getConnection();
				String query = "select id from movies where title=?";
				PreparedStatement statement = dbcon.prepareStatement(query);
				statement.setString(1, title);
				ResultSet rs = statement.executeQuery();
				if(rs.next())
				{
					id = rs.getString("id");
				}
				rs.close();
				statement.close();
				dbcon.close();
			}

			catch (Exception e) {
				e.printStackTrace();
				response.setStatus(500);
			}
		}
		else
			id = request.getParameter("id");
		System.out.println("id: " + id);

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);

			String query = 	"select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(distinct stars.id order by stars.id) as stars_id,"+
					"substring_index(group_concat(distinct genres.name separator ','), ',', 3) as g,"+
					"group_concat(distinct stars.name order by stars.id) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId"+
					" left join ratings on ratings.movieId=movies.id inner join genres on "+
					" genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId "+
					" inner join stars on stars_in_movies.starId=stars.id and movies.id =? group by movies.id";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();
			ArrayList<String> temp = new ArrayList<String>();
			ArrayList<String> temp2 = new ArrayList<String>();
			JsonObject jsonObject = new JsonObject();


			// Iterate through each row of rs
			while (rs.next()) {
//				System.out.println("stars_id = " + rs.getString("stars_id"));
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_genres = rs.getString("g");
				// String movie_stars = rs.getString("stars");
				Collections.addAll(temp, rs.getString("stars_id").split(","));
				Collections.addAll(temp2, rs.getString("stars").split(","));
				String movie_rating = rs.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs
				jsonObject.addProperty("movie_title", movie_title);
				jsonObject.addProperty("movie_year", movie_year);
				jsonObject.addProperty("movie_director", movie_director);
				jsonObject.addProperty("movie_genres", movie_genres);
				//jsonObject.addProperty("movie_stars", movie_stars);
				jsonObject.addProperty("movie_rating", movie_rating);
				//jsonObject.addProperty("stars_id",stars_id);
				jsonArray.add(jsonObject);
			}

			PreparedStatement statement2 = null;
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 0; i < temp.size(); i++) {
				String query2 = "SELECT starID, COUNT(*) as sample " +
						"FROM stars_in_movies, movies " +
						"where stars_in_movies.movieId = movies.id " +
						"and starID = ? " +
						"GROUP BY starID";
				statement2 = dbcon.prepareStatement(query2);
				statement2.setString(1, temp.get(i));
				ResultSet rs2 = statement2.executeQuery();
				rs2.next();
				value.add(rs2.getString("sample"));

				if (i == temp.size() - 1) {
					rs2.close();
				}
			}
			System.out.println("value = " + value);
			if (statement2 != null)
				statement2.close();
			System.out.println("temp = "+temp);
			System.out.println("temp2 = " + temp2);

			for (int n = 0; n < temp.size(); n++) {
				System.out.println("checkvalue" + value);
				for (int m = 0; m < temp.size() - n - 1; m++) {

					if (Integer.parseInt(value.get(m))< Integer.parseInt(value.get(m+1))) {
						String swapString = value.get(m);
						value.set(m, value.get(m + 1));
						value.set(m + 1, swapString);
						String swapString2 = temp.get(m);
						temp.set(m, temp.get(m + 1));
						temp.set(m + 1, swapString2);
						String swapString3 = temp2.get(m);
						temp2.set(m, temp2.get(m + 1));
						temp2.set(m + 1, swapString3);
					} else if (Integer.parseInt(value.get(m))== Integer.parseInt(value.get(m+1))) {
						if (temp2.get(m).compareTo(temp2.get(m + 1)) > 0) {
							String swapString = value.get(m);
							value.set(m, value.get(m + 1));
							value.set(m + 1, swapString);
							String swapString2 = temp.get(m);
							temp.set(m, temp.get(m + 1));
							temp.set(m + 1, swapString2);
							String swapString3 = temp2.get(m);
							temp2.set(m, temp2.get(m + 1));
							temp2.set(m + 1, swapString3);
						}
					}
				}
			}
			System.out.println("value = " + value);
			String k2 = "";
			String k = "";
//			System.out.println("stars_id list =" + temp);
//			System.out.println("stars list =" + temp2);
//			System.out.println("number list =" + value);

			for (int i = 0; i < temp.size(); i++) {
//				System.out.println("stars_id that we get : " + temp.get(i));
//				jsonArray.get(i).addProperty("stars_id", temp.get(i));

				if (i == temp.size() - 1) {
					k += temp2.get(i);
					k2 += temp.get(i);
				} else {
					k += temp2.get(i) + ",";
					k2 += temp.get(i) + ",";
				}
			}
			jsonObject.addProperty("movie_stars", k);
			jsonObject.addProperty("stars_id", k2);
			jsonArray.add(jsonObject);
			rs.close();
			// write JSON string to output
			out.write(jsonArray.toString());
			// set response status to 200 (OK)
			response.setStatus(200);
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