import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginEmployeeServlet", urlPatterns = "/_dashboard")
public class LoginEmployeeServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();

		// clear user from session
		if (session.getAttribute("user") != null) {
			session.removeAttribute("user");
		}

		// clear cart from session
		Map<String, Integer> cartList = (HashMap<String, Integer>) session.getAttribute("cartList");
		if (cartList != null) {
			synchronized (cartList) {
				cartList.clear();
			}
		}

		JsonObject responseJsonObject = new JsonObject();

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		String email = request.getParameter("email");
		String fullname = ""; //
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

		// now try verifying credentials
		try {
			if (VerifyPassword.verifyCredentialsEmployee(email, password)) { // login success: set this employee into the session
				System.out.println("credentials verified");
				request.getSession().setAttribute("employee", new Employee(email));
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
			}
			else {
				System.out.println("invalid credentials");
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Invalid email or password");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Invalid email or password");
		}
		finally {
			response.getWriter().write(responseJsonObject.toString());
		}
	}
}
