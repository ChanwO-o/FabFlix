import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		// Retrieve data named "cartList" from session
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");
		if (cartList != null) { // clear cart on logout
			synchronized (cartList) {
				cartList.clear();
			}
		}

		JsonObject responseJsonObject = new JsonObject();

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		String email = request.getParameter("email");
		int idResult = -1; // get id from query below
		String password = request.getParameter("password");

		try { // first, check if captcha success
			RecaptchaVerifyUtils.verify(gRecaptchaResponse);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Invalid Recaptcha response; login failed");
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "captcha failed");
			response.getWriter().write(responseJsonObject.toString());
			return;
		}

		try {
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);

			String query = "SELECT id, email, password from customers where email = ? and password = ?";
			PreparedStatement statement = dbcon.prepareStatement(query);

			statement.setString(1, email);
			statement.setString(2, password);

			ResultSet rs = statement.executeQuery();
			dbcon.commit();

			if (rs.next()) { // result set has at least one entry: login success
				idResult = rs.getInt("id"); // assign user id
				String emailResult = rs.getString("email");
				String pwResult = rs.getString("password");
				System.out.println("idResult: " + idResult + " emailResult: " + emailResult + " pwResult: " + pwResult);

				// login success: set this user into the session
				request.getSession().setAttribute("email", new User(email, idResult));
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
			}
			else { // login fail
				System.out.println("ResultSet returned no results. Login failed.");
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Invalid email or password");
			}

			response.getWriter().write(responseJsonObject.toString());
			rs.close();
			statement.close();
			dbcon.close();
		}
		catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
