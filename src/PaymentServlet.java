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
			else { // payment success
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
				synchronized (cartList) {
					cartList.clear();
				}
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
