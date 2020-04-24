import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.deploy.security.SelectableSecurityManager;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameters from url request
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genres = request.getParameter("genres");
        String title_start=request.getParameter("title_start");
        String sortby = request.getParameter("sortby");
        String sortorder = request.getParameter("sortorder");
        System.out.println("received params: " + title + " " + year + " " + director + " " + star + " " + genres + " " + title_start + " " + sortby + " " + sortorder);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        if (genres == null || genres == "") //check browse by genres
        {
            if ((title == null || title.equals("")) && (year == null || year.equals("")) &&
                    (director == null || director.equals("")) && (star == null || star.equals("")))
            {
                if((title_start==null)||title_start=="")
                {
                    try {
                        // Get a connection from dataSource
                        Connection dbcon = dataSource.getConnection();

                        // Declare our statement
                        Statement statement = dbcon.createStatement();

                        String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
                                ", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
                                "group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId " +
                                "left join ratings on ratings.movieId=movies.id inner join genres on genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
                                "inner join stars on stars_in_movies.starId=stars.id group by movies.id";

                        // add sort options to query
                        if (sortby != null) {
                            if (sortby.equals("title"))
                                query += " order by movies.title";
                            else if (sortby.equals("rating"))
                                query += " order by ratings.rating";
                        }
                        if (sortorder != null && sortorder.equals("desc"))
                            query += " desc";
                        query += " limit 20";

                        // Perform the query
                        ResultSet rs = statement.executeQuery(query);
                        JsonArray jsonArray = new JsonArray();

                        // Iterate through each row of rs

                        while (rs.next()) {
                            String movie_id = rs.getString("id");
                            String star_id = rs.getString("star_id");
                            String movie_title = rs.getString("title");
                            String movie_year = rs.getString("year");
                            String movie_director = rs.getString("director");
                            String movie_rating = rs.getString("rating");
                            String movie_genres = rs.getString("genres");
                            String movie_stars = rs.getString("stars");


                            // Create a JsonObject based on the data we retrieve from rs
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("movie_id", movie_id);
                            jsonObject.addProperty("star_id", star_id);
                            jsonObject.addProperty("movie_title", movie_title);
                            jsonObject.addProperty("movie_year", movie_year);
                            jsonObject.addProperty("movie_director", movie_director);
                            jsonObject.addProperty("movie_rating", movie_rating);
                            jsonObject.addProperty("movie_genres", movie_genres);
                            jsonObject.addProperty("movie_stars", movie_stars);
                            jsonArray.add(jsonObject);


                        }

                        // write JSON string to output
                        out.write(jsonArray.toString());
                        // set response status to 200 (OK)
                        response.setStatus(200);

                        rs.close();
                        statement.close();
                        dbcon.close();


                    } catch (Exception e) {

                        // write error message JSON object to output
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("errorMessage", e.getMessage());
                        out.write(jsonObject.toString());

                        // set reponse status to 500 (Internal Server Error)
                        response.setStatus(500);

                    }
                    out.close();
                }
                else // browsed by title_start
                {
                    try {
                        // Get a connection from dataSource
                        Connection dbcon = dataSource.getConnection();

                        // Declare our statement
                        Statement statement = dbcon.createStatement();

                        String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
                                ", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
                                "group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId " +
                                "left join ratings on ratings.movieId=movies.id inner join genres on genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
                                "inner join stars on stars_in_movies.starId=stars.id ";

                        if (!title_start.isEmpty())
                        {
                            if(title_start.contains("*"))
                            {
                                query += " and movies.title not REGEXP '^[0-9a-z]'";
                            }
                            else
                                query += " and movies.title like '" + title_start + "%' ";
                        }
                        query += " group by movies.id ";

                        // add sort options to query
                        if (sortby != null) {
                            if (sortby.equals("title"))
                                query += " order by movies.title";
                            else if (sortby.equals("rating"))
                                query += " order by ratings.rating";
                        }
                        if (sortorder != null && sortorder.equals("desc"))
                            query += " desc";

                        // Perform the query
                        ResultSet rs = statement.executeQuery(query);
                        JsonArray jsonArray = new JsonArray();

                        // Iterate through each row of rs

                        while (rs.next()) {
                            String movie_id = rs.getString("id");
                            String star_id = rs.getString("star_id");
                            String movie_title = rs.getString("title");
                            String movie_year = rs.getString("year");
                            String movie_director = rs.getString("director");
                            String movie_rating = rs.getString("rating");
                            String movie_genres = rs.getString("genres");
                            String movie_stars = rs.getString("stars");


                            // Create a JsonObject based on the data we retrieve from rs
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("movie_id", movie_id);
                            jsonObject.addProperty("star_id", star_id);
                            jsonObject.addProperty("movie_title", movie_title);
                            jsonObject.addProperty("movie_year", movie_year);
                            jsonObject.addProperty("movie_director", movie_director);
                            jsonObject.addProperty("movie_rating", movie_rating);
                            jsonObject.addProperty("movie_genres", movie_genres);
                            jsonObject.addProperty("movie_stars", movie_stars);
                            jsonArray.add(jsonObject);


                        }

                        // write JSON string to output
                        out.write(jsonArray.toString());
                        // set response status to 200 (OK)
                        response.setStatus(200);

                        rs.close();
                        statement.close();
                        dbcon.close();


                    } catch (Exception e) {

                        // write error message JSON object to output
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("errorMessage", e.getMessage());
                        out.write(jsonObject.toString());

                        // set reponse status to 500 (Internal Server Error)
                        response.setStatus(500);

                    }
                    out.close();
                }
            } else { // user did advanced search

                try {
                    // Get a connection from dataSource
                    Connection dbcon = dataSource.getConnection();

                    // Declare our statement
                    Statement statement = dbcon.createStatement();

                    String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
                            ", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
                            "group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId" +
                            " left join ratings on ratings.movieId=movies.id inner join genres on " +
                            "genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
                            "inner join stars on stars_in_movies.starId=stars.id ";

                    if (!title.isEmpty())
                        query += " and movies.title like '%" + title + "%' ";
                    if (!year.isEmpty())
                        query += " and movies.year = " + year;
                    if (!director.isEmpty())
                        query += " and movies.director like '%" + director + "%' ";
                    if (!star.isEmpty())
                        query += " and stars.name like '%" + star + "%' ";

                    query += " group by movies.id ";

                    // add sort options to query
                    if (sortby != null) {
                        if (sortby.equals("title"))
                            query += " order by movies.title";
                        else if (sortby.equals("rating"))
                            query += " order by ratings.rating";
                    }
                    if (sortorder != null && sortorder.equals("desc"))
                        query += " desc";

                    // Perform the query
                    ResultSet rs = statement.executeQuery(query);

                    JsonArray jsonArray = new JsonArray();

                    // Iterate through each row of rs

                    while (rs.next()) {
                        String movie_id = rs.getString("id");
                        String star_id = rs.getString("star_id");
                        String movie_title = rs.getString("title");
                        String movie_year = rs.getString("year");
                        String movie_director = rs.getString("director");
                        String movie_rating = rs.getString("rating");
                        String movie_genres = rs.getString("genres");
                        String movie_stars = rs.getString("stars");

                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("star_id", star_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_year", movie_year);
                        jsonObject.addProperty("movie_director", movie_director);
                        jsonObject.addProperty("movie_rating", movie_rating);
                        jsonObject.addProperty("movie_genres", movie_genres);
                        jsonObject.addProperty("movie_stars", movie_stars);
                        jsonArray.add(jsonObject);

                    }

                    // write JSON string to output
                    out.write(jsonArray.toString());
                    // set response status to 200 (OK)
                    response.setStatus(200);

                    rs.close();
                    statement.close();
                    dbcon.close();
                } catch (Exception e) {

                    // write error message JSON object to output
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("errorMessage", e.getMessage());
                    out.write(jsonObject.toString());

                    // set reponse status to 500 (Internal Server Error)
                    response.setStatus(500);

                }
                out.close();
            }
        }
        else // browsed by genre
        {
            try {
                // Get a connection from dataSource
                Connection dbcon = dataSource.getConnection();

                // Declare our statement
                Statement statement = dbcon.createStatement();

                String query = "select movies.id,movies.title,movies.year,movies.director,ratings.rating,group_concat(stars.id) as star_id" +
                        ", substring_index(group_concat(distinct genres.name separator ','), ',', 3) as genres, " +
                        "group_concat(stars.name) as stars from movies inner join genres_in_movies on movies.id=genres_in_movies.movieId" +
                        " left join ratings on ratings.movieId=movies.id inner join genres on " +
                        "genres.id=genres_in_movies.genreId inner join stars_in_movies on movies.id=stars_in_movies.movieId " +
                        "inner join stars on stars_in_movies.starId=stars.id group by movies.id";

                // add sort options to query
                if (sortby != null) {
                    if (sortby.equals("title"))
                        query += " order by movies.title";
                    else if (sortby.equals("rating"))
                        query += " order by ratings.rating";
                }
                if (sortorder != null && sortorder.equals("desc"))
                    query += " desc";

                // Perform the query
                ResultSet rs = statement.executeQuery(query);
                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs

                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String star_id = rs.getString("star_id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_rating = rs.getString("rating");
                    String movie_genres = rs.getString("genres");
                    String movie_stars = rs.getString("stars");

                    // Create a JsonObject based on the data we retrieve from rs
//                    System.out.println("movie_genres:" + movie_genres);
                    //System.out.println("Genres that query gives:" +genres);
                    if(movie_genres.contains(genres)) {
                        //System.out.println(movie_genres);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("star_id", star_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_year", movie_year);
                        jsonObject.addProperty("movie_director", movie_director);
                        jsonObject.addProperty("movie_rating", movie_rating);
                        jsonObject.addProperty("movie_genres", movie_genres);
                        jsonObject.addProperty("movie_stars", movie_stars);
                        jsonArray.add(jsonObject);
                    }

                }

                // write JSON string to output
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

                rs.close();
                statement.close();
                dbcon.close();


            } catch (Exception e) {

                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // set reponse status to 500 (Internal Server Error)
                response.setStatus(500);

            }
            out.close();
        }
    }
}
