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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
        if (cartList != null) { // clear cart on logout
            synchronized (cartList) {
                cartList.clear();
            }
        }

        String email = request.getParameter("email");
        int id = -1; // get id from query below
        String password = request.getParameter("password");

        try
        {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            String query = "SELECT id, email, password from customers";
            ResultSet rs = statement.executeQuery(query);
            boolean emailSuccess = false;
            boolean pwSuccess =false;
            while(rs.next())
            {
                id = rs.getInt("id"); // assign user id
                String email_list = rs.getString("email");
                String pw_list = rs.getString("password");
                if(emailSuccess)
                    break;
                if(email.equals(email_list))
                {
                    emailSuccess = true;
                    pwSuccess= password.equals(pw_list);
                }
            }
            JsonObject responseJsonObject = new JsonObject();

            if (emailSuccess && pwSuccess)
            {
                // Login success:
                // set this user into the session
                request.getSession().setAttribute("email", new User(email, id));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else
            {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                if (!emailSuccess)
                {
                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
                }
                else
                {
                    responseJsonObject.addProperty("message", "incorrect password");
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
}
