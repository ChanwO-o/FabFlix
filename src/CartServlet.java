import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Declaring a WebServlet called ItemsServlet, which maps to url "/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Get a instance of current session on the request
		HttpSession session = request.getSession();

		// Retrieve data named "previousItems" from session
		ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

		// If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
		// ArrayList for the user
		if (previousItems == null) {
			System.out.println("no previousItems found; creating new previousItems list for user");
			// Add the newly created ArrayList to session, so that it could be retrieved next time
			session.setAttribute("previousItems", new ArrayList<>());
		}

		String newMovieId = request.getParameter("id"); // Get parameter that sent by GET request url
		System.out.println("newMovieId: " + newMovieId);

		// In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we
		// lock the ArrayList while updating
		synchronized (previousItems) {
			if (newMovieId != null) {
				previousItems.add(newMovieId); // Add the new item to the previousItems ArrayList
			}
			// Display the current previousItems ArrayList
			System.out.println("items: " + previousItems);
		}
	}
}
