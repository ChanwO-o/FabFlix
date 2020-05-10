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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		System.out.println(request.getRequestURI());

		// for add star
		String name = request.getParameter("name");
		String birthyear = request.getParameter("birthyear");
		// for add movie
		String title = request.getParameter("title");
		String director = request.getParameter("director");
		String year = request.getParameter("year");
		String genre = request.getParameter("genre");
		String star_name = request.getParameter("star_name");

		System.out.println("DashboardServlet doGet() params name: " + name + " birthyear: " + birthyear + "title: " + title + " director: " + director +
				" year: " + year + " genre: " + genre + " star_name: " + star_name);

		if (name == null) { // add movie
			if(title == null || director == null || year == null || genre == null || star_name == null)
				return;
			try{
				Connection dbcon = dataSource.getConnection();
				dbcon.setAutoCommit(false);
				String query = "SELECT id FROM movies ORDER BY id DESC LIMIT 1;";
				Statement statement = dbcon.createStatement();
				ResultSet rs = statement.executeQuery(query);
				dbcon.commit();
				rs.next();
				String lastMovieId = rs.getString("id");
				System.out.println("last movie id: " + lastMovieId);

				String newMovieId = "tt" + Integer.parseInt(lastMovieId.substring(2)) + 1;

				// now get star id (existing/new)
				String getLastStarQuery = "SELECT id FROM stars ORDER BY id DESC LIMIT 1;"; // get last id number from stars
				Statement getLastStarStatement = dbcon.createStatement();
				ResultSet rs2 = getLastStarStatement.executeQuery(getLastStarQuery);
				dbcon.commit();
				rs2.next();
				String lastStarId = rs2.getString("id");
				System.out.println("last star id found: " + lastStarId);
				String newId = "nm" + (Integer.parseInt(lastStarId.substring(2)) + 1); // get integer portion of id + 1
				System.out.println("new star id: " + newId);


				CallableStatement cstatement = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?,?)}");
				cstatement.setString(1, newMovieId);
				cstatement.setString(2, title);
				cstatement.setInt(3, Integer.parseInt(year));
				cstatement.setString(4, director);
				cstatement.setString(5, star_name);
				cstatement.setString(6, newId);
				cstatement.setString(7, genre);
				System.out.println(cstatement);
				ResultSet rs3 = cstatement.executeQuery();
				dbcon.commit();
				rs3.next();
				rs.close();
				rs3.close();
				dbcon.close();


			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else { // add star
			try {
				Connection dbcon = dataSource.getConnection();
				dbcon.setAutoCommit(false);

				String getLastStarQuery = "SELECT id FROM stars ORDER BY id DESC LIMIT 1;"; // get last id number from stars
				Statement getLastStarStatement = dbcon.createStatement();
				ResultSet rs = getLastStarStatement.executeQuery(getLastStarQuery);
				dbcon.commit();

				rs.next();
				String lastStarId = rs.getString("id");
				System.out.println("last star id: " + lastStarId);

				String newId = "nm" + (Integer.parseInt(lastStarId.substring(2)) + 1); // get integer portion of id + 1
				System.out.println("new star id: " + newId);
				dbcon.close();
				Connection dbcon2 = dataSource.getConnection();
				dbcon2.setAutoCommit(false);
				String insertStarQuery = "INSERT INTO stars VALUES (?, ?, ?)";
				//
				PreparedStatement statement = dbcon2.prepareStatement(insertStarQuery);
				statement.setString(1, newId);
				statement.setString(2, name);

				if (birthyear == null || birthyear.isEmpty() || !birthyear.matches("-?\\d+")) // birthyear is null/empty/not an int
					statement.setInt(3, 0);
				else
					statement.setInt(3, Integer.parseInt(birthyear));
				//
				System.out.println("insertStarQuery query: " + insertStarQuery);
				statement.executeUpdate();
				dbcon2.commit();

				//	JsonObject responseJsonObject = new JsonObject();
				//			response.getWriter().write(responseJsonObject.toString());
				rs.close();
				getLastStarStatement.close();
				//			statement.close();
				dbcon2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

	}
}
