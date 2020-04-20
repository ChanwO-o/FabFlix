import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try
        {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            String query = "SELECT email, password from customers";
            ResultSet rs = statement.executeQuery(query);
            boolean emailSuccess = false;
            boolean pwSuccess =false;
            while(rs.next())
            {
                String email_list = rs.getString("email");
                String pw_list = rs.getString("password");
                if(emailSuccess==true)
                    break;
                if(username.equals(email_list))
                {
                    emailSuccess = true;
                    if(password.equals(pw_list))
                    {
                        pwSuccess=true;
                    }
                    else
                    {
                        pwSuccess=false;
                    }
                }

            }
            JsonObject responseJsonObject = new JsonObject();

            if (emailSuccess && pwSuccess)
            {
                // Login success:
                // set this user into the session
                request.getSession().setAttribute("email", new User(username));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            }
            else
            {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                if (!emailSuccess)
                {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
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
