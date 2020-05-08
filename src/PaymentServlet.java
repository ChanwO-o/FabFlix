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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Get a instance of current session on the request
		HttpSession session = request.getSession();

		PrintWriter out = response.getWriter();
		// Retrieve data named "cartList" from session
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");

		// If "cartList" is not found on session, means this is a new user, thus we create a new cartList for the user
		if (cartList == null) {
//			System.out.println("no cartList found; creating new cartList for user");
			// Add the newly created cartList to session, so that it could be retrieved next time
			session.setAttribute("cartList", new HashMap<String, Integer>());
			cartList = (HashMap<String, Integer>) session.getAttribute("cartList"); // reassign cartList variable to newly created list
		}

		double total = 0.00;
		for (Map.Entry<String, Integer> entry : cartList.entrySet()) {
			total += generateMoviePrice(entry.getKey()) * entry.getValue();
		}
//		System.out.println("total: " + total);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("grand_total", total);
		out.write(jsonObject.toString());
		out.close();
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		// Retrieve data named "cartList" from session
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");
		if (cartList == null)
			return;

		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String card = request.getParameter("card");
		String exp = request.getParameter("exp");
		System.out.println("fname: " + fname + " lname: " + lname + " card: " + card + " exp: " + exp);

		try {
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);

			String query = "select * from creditcards " +
					"where binary id = ? and binary firstName = ? and binary lastName = ? and binary expiration = ?";
			System.out.println("payment query: " + query);

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, card);
			statement.setString(2, fname);
			statement.setString(3, lname);
			statement.setString(4, exp);
			ResultSet rs = statement.executeQuery();

			JsonObject responseJsonObject = new JsonObject();

			if (!rs.isBeforeFirst()) { // wrong card information given
//				System.out.println("No data");
				responseJsonObject.addProperty("message", " Wrong Information");
			} else { // payment success: add sale entries
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");

				// get customer id
				User user = (User) session.getAttribute("user");
				int customerId = user.getId();
//				System.out.println("Sale customerId: " + customerId);

				synchronized (cartList) { // add a record to sales table for each entry in cartList
					// construct sales query
					String salesQuery = "INSERT INTO sales VALUES ";

					// keep track of sql param pairs (customerId & movieId) with two ArrayLists, each pair will be stored in same index
					ArrayList<Integer> customerIdsParamList = new ArrayList<>();
					ArrayList<String> movieIdsParamList = new ArrayList<>();

					int index = 0;
					for (Map.Entry<String, Integer> entry : cartList.entrySet()) {
						String movieEntryQuery = "";
						for (int i = 0; i < entry.getValue(); ++i) { // iterate through movie quantity
//							movieEntryQuery += "(NULL," + customerId + ", '" + entry.getKey() + "', CURDATE())";
							movieEntryQuery += "(NULL, ?, ?, CURDATE())";

							// add params to ArrayLists that will replace the ?s
							customerIdsParamList.add(customerId);
							movieIdsParamList.add(entry.getKey());

							if (i < entry.getValue() - 1)
								movieEntryQuery += ","; // inner comma
						}
						if (index < cartList.entrySet().size() - 1) {
							movieEntryQuery += ","; // outer comma
							index++;
						}
						System.out.println("PaymentServlet movieEntryQuery: " + movieEntryQuery);
						salesQuery += movieEntryQuery;
					}

					// finished salesQuery
					System.out.println("PaymentServlet salesQuery: " + salesQuery);
					PreparedStatement statementInsertSales = dbcon.prepareStatement(salesQuery);

					// apply parameter pairs to salesQuery PreparedStatement
					System.out.println("customerIdsParamList: " + customerIdsParamList);
					System.out.println("movieIdsParamList: " + movieIdsParamList);
					for (int i = 0; i < customerIdsParamList.size(); ++i) {
						int customerIdParamIndex = 2 * i + 1;
						int movieIdParamIndex = customerIdParamIndex + 1;
						System.out.println("customerIdParamIndex: " + customerIdParamIndex);
						System.out.println("movieIdParamIndex: " + movieIdParamIndex);
						statementInsertSales.setInt(customerIdParamIndex, customerIdsParamList.get(i));
						statementInsertSales.setString(movieIdParamIndex, movieIdsParamList.get(i));
					}

					System.out.println("statementInsertSales: " + statementInsertSales);
					statementInsertSales.executeQuery(); // perform insert to sales table
//					statementInsertSales.executeUpdate(); // perform insert to sales table
					statementInsertSales.close();
					cartList.clear(); // clear cart once purchase is complete
				}
			}
			response.getWriter().write(responseJsonObject.toString());
			rs.close();
			statement.close();
			dbcon.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private double generateMoviePrice(String movie_id) {
		return 15.00;
	}
}
