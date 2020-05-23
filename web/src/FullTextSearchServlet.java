import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static java.sql.DriverManager.getConnection;

// server endpoint URL
@WebServlet("/fulltext")
public class FullTextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    public static HashMap<String,String> movielist=new HashMap<>();
    int i;
    static
    {
        String sqlId = "mytestuser";
        String sqlPw = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            int count=0;
            Connection dbcon = getConnection(loginUrl, sqlId, sqlPw);
            String query = "Select id,title from movies";
            Statement statement = dbcon.createStatement();
            ResultSet rs= statement.executeQuery(query);
            while(rs.next())
            {
                count++;
                String id = rs.getString("id");
                String title=rs.getString("title");
                System.out.println("ID = " + id + "Title = " + title);
                movielist.put(id,title);
            }
            System.out.println(count);
            rs.close();
            statement.close();
            dbcon.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public FullTextSearchServlet() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // tokenize user's search query
            StringBuilder tokenStringBuilder = new StringBuilder("'");
            String[] tokens = query.split(" ");
            for (String token : tokens) {
                tokenStringBuilder.append("+").append(token).append("* ");
            }
            tokenStringBuilder.append("'");

            String sqlId = "mytestuser";
            String sqlPw = "mypassword";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
            Connection dbcon = getConnection(loginUrl, sqlId, sqlPw);
            // append tokens to query to perform full text search
            String fullTextSearchQuery = "SELECT * FROM ft WHERE MATCH (entry) AGAINST (? IN BOOLEAN MODE) limit 10;";
            PreparedStatement statement = dbcon.prepareStatement(fullTextSearchQuery);
            statement.setString(1, tokenStringBuilder.toString());
            ResultSet rs = statement.executeQuery();

            while(rs.next())
            {
                jsonArray.add(generateJsonObject(rs.getInt("entryID"), rs.getString("entry")));
            }
            System.out.println("fullTextSearchQuery: " + fullTextSearchQuery);
            response.getWriter().write(jsonArray.toString());
            rs.close();
            statement.close();
            dbcon.close();

            return;



            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
//            int count=1;
////            String sqlId = "mytestuser";
////            String sqlPw = "mypassword";
////            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
////            Connection dbcon = getConnection(loginUrl, sqlId, sqlPw);
//
////            System.out.println("movielist = " + movielist);
//            System.out.println(movielist);
//            for (String id : movielist.keySet())
//            {
//                String movieName = movielist.get(id);
////                System.out.println("id="+id + " title= " + movieName);
//
//                if (movieName.toLowerCase().contains(query.toLowerCase()))
//                {
////                    System.out.println("check2");
//                    jsonArray.add(generateJsonObject(id, movieName));
//                }
//            }
//
//            response.getWriter().write(jsonArray.toString());
//            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(Integer movieID, String movieName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}