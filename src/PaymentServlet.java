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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		// Retrieve data named "cartList" from session
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");

		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String card = request.getParameter("card");
		String exp = request.getParameter("exp");
		System.out.println("fname = " + fname);
		System.out.println("lname = " + lname);
		System.out.println("card = " + card);
		System.out.println("exp = " + exp);

		try
		{
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			Statement statement2 = dbcon.createStatement();

			String query = "select * from creditcards " +
					"where id= '"+card+"'"+ " and firstName='" + fname + "'" +  "and lastName='" + lname + "'" + " and expiration='"+ exp + "'";
			ResultSet rs = statement.executeQuery(query);
			boolean cardsuccess = false;
			boolean fnamesuccess=false;
			boolean lnamesuccess=false;
			boolean expsuccess=false;

			JsonObject responseJsonObject = new JsonObject();

			if (!rs.isBeforeFirst() ) {
				System.out.println("No data");
				responseJsonObject.addProperty("message",  " Wrong Information");
			}
			else { // payment success: add sale entries
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");

				// get customer id
				User user = (User) session.getAttribute("email");
				int customerId = user.getId();
//				System.out.println("Sale customerId: " + customerId);
				System.out.println("Sale cartList: " + cartList);

				if (cartList == null)
					return;

//				Statement statement = dbcon.createStatement();

				synchronized (cartList) { // add a record to sales table for each entry in cartList

					// construct sales query
					String salesQuery = "INSERT INTO sales VALUES ";
					int count = 0;
					for (Map.Entry<String, Integer> entry : cartList.entrySet()) {
						String movieEntryQuery = "";
						for (int i = 0; i < entry.getValue(); ++i) { // iterate through movie quantity
							System.out.println("i: " + i + " getValue: " + entry.getValue());
							movieEntryQuery += "(NULL," + customerId + ", '" + entry.getKey() + "', CURDATE())";
							if (i < entry.getValue() - 1)
								movieEntryQuery += ","; // inner comma
						}
						if (count < cartList.entrySet().size() - 1) {
							movieEntryQuery += ","; // outer comma
							count++;
						}
						salesQuery += movieEntryQuery;
					}
					System.out.println("PaymentServlet query: " + salesQuery);
					statement2.executeUpdate(salesQuery); // perform insert to sales table

					cartList.clear(); // clear cart once purchase is complete
				}

			}
			response.getWriter().write(responseJsonObject.toString());
			rs.close();
			statement.close();
			statement2.close();
			dbcon.close();
		}
		catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
