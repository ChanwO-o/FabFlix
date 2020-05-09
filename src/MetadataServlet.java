import java.io.*;
import java.sql.*;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

/**
 * Servlet implementation class ProvideMeta
 */
@WebServlet("/metadata")
public class MetadataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection dbcon;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     * @see HttpServlet#HttpServlet()
     */
    public MetadataServlet() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        try {
            response.setContentType("text/html");
            HttpSession session = request.getSession(false);

            System.out.println("HERE");
            String p= request.getParameter("showMetadata");
            System.out.println(p);
            if (request.getParameter("showMetadata") != null)
            {

                Connection dbcon = dataSource.getConnection();
                DatabaseMetaData metadata = dbcon.getMetaData();

                ResultSet rs = metadata.getTables(null, null, "%", null);

                out.println("<button onclick="+'"'+"location.href='dashboard.html'"+'"'+">Back to dashboard</button>");
                out.println("<br>");
                out.println("<br>");

                out.println("<h2> METADATA INFORMATION </h2> <br>");

                while (rs.next())
                {
                    String tableName = rs.getString(3);
                    out.println("Table Name: " + tableName + "<br>");

                    String query = "select * from " + tableName;
                    Statement statement = dbcon.createStatement();
                    ResultSet rs2 = statement.executeQuery(query);
                    ResultSetMetaData MD = rs2.getMetaData();

                    out.println("Total columns: " + MD.getColumnCount() + " columns." + "<br>");
                    ResultSet cols = metadata.getColumns(null, null, tableName, null);

                    while (cols.next()) {
                        out.print("Column attribute: ");
                        out.print(cols.getString("COLUMN_NAME"));
                        out.print(", type: ");
                        out.print(cols.getString("TYPE_NAME"));
                        out.println("." + "<br>");
                    }
                    out.println("<br>");

                }

            }

            dbcon.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not print metadata.");
        }
    }

}