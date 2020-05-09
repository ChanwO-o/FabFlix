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

		System.out.println(request.getRequestURI());

		String name = request.getParameter("name");
		String birthyear = request.getParameter("birthyear");

		System.out.println("DashboardServlet doGet() params name: " + name + " birthyear: " + birthyear);

		if (name == null)
			return;

		PrintWriter out = response.getWriter();
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
			if (birthyear == null || birthyear.isEmpty())
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
		out.close();
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

	}
}
