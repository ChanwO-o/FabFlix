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
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called ItemsServlet, which maps to url "/items"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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

		double total = 0.00;
		for (Map.Entry<String, Integer> entry : cartList.entrySet()) {
			total += generateMoviePrice(entry.getKey()) * entry.getValue();
		}
		System.out.println("total: " + total);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("grand_total", total);
		out.write(jsonObject.toString());

		out.close();
	}

	private double generateMoviePrice(String movie_id) {
		return 15.00;
	}
}
