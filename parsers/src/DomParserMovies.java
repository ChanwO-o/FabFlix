import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DomParserMovies {
	List<ParsedMovie> myParsedMovies;
	Set<ParsedGenre> myParsedGenres;
	Document dom;
	BufferedWriter writer;

	public DomParserMovies() {
		//create a list to hold the Movie objects
		myParsedMovies = new ArrayList<>();
		myParsedGenres = new HashSet<>();
	}

	public void run(String filename) throws IOException {
		String inconsistenciesFilename = "parsers/inconsistencies-" + filename + ".txt";
		writer = new BufferedWriter(new FileWriter(inconsistenciesFilename));

		//parse the xml file and get the dom object
		parseXmlFile(filename);

		//get each movie element and create a Movie object
		parseDocument();

		//Iterate through the list and print the data
        printData();

		//Insert parsed data into our database
		insertMovieData();
		insertGenreData();
		insertGenresInMoviesData();

		writer.close();
	}

	private void parseXmlFile(String filename) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse("parsers/" + filename);

		} catch (ParserConfigurationException | SAXException | IOException pce) {
			pce.printStackTrace();
		}
	}

	/**
	 * Parse through entire xml document and populate myMovies list with Movie objects
	 */
	private void parseDocument() throws IOException {
		//get the root elememt
		Element root = dom.getDocumentElement();

		//get list of <director + films> pairs
		NodeList directorfilmsTags = root.getElementsByTagName("directorfilms");
		if (directorfilmsTags == null || directorfilmsTags.getLength() == 0) {
			System.out.println("directorfilmstags is empty/null");
			return; // empty document
		}

		for (int i = 0; i < directorfilmsTags.getLength(); ++i) {
//			System.out.println("directorfilmstag # " + i);
			// the directorfilms tag to work on
			Node directorfilmsTag = directorfilmsTags.item(i);

			// get director / films
			String director = null;
			ArrayList<ParsedMovie> parsedMovies = null;
			for (int j = 0; j < directorfilmsTag.getChildNodes().getLength(); ++j) {
				Node directorOrFilms = directorfilmsTag.getChildNodes().item(j);
//				System.out.println("directorOrFilm: " + directorOrFilms.getNodeName());
				if (directorOrFilms.getNodeName().equals("director")) {
					director = getDirectorName(directorOrFilms);
//					System.out.println("director name found: " + director);
				}
				else if (directorOrFilms.getNodeName().equals("films")) {
					parsedMovies = getMovies(directorOrFilms, director);
				}
			}

			if (parsedMovies != null) {
//				System.out.println("director: " + director + "; parsed " + movies.size() + " movies");
				myParsedMovies.addAll(parsedMovies);
			}
		}
	}

	/**
	 * Get director name from a <director></director> node
	 */
	private String getDirectorName(Node directorNode) {
		NodeList directorData = directorNode.getChildNodes();
		for (int i = 0; i < directorData.getLength(); ++i) {
//			System.out.println(directorData.item(i).getNodeName());
			if (directorData.item(i).getNodeName().equals("dirname") || directorData.item(i).getNodeName().equals("dirn"))
				return directorData.item(i).getTextContent();
		}
		return null;
	}

	/**
	 * Get list of movies from a <films></films> node
	 */
	private ArrayList<ParsedMovie> getMovies(Node filmsNode, String director) throws IOException {
		ArrayList<ParsedMovie> result = new ArrayList<>();
		NodeList filmNodes = filmsNode.getChildNodes();
		for (int i = 0; i < filmNodes.getLength(); ++i) {
			Node filmNode = filmNodes.item(i);
			NodeList filmData = filmNode.getChildNodes();

			String title = null;
			int year = 0;
			List<ParsedGenre> genresForThisMovie = new ArrayList<>();

			for (int j = 0; j < filmData.getLength(); ++j) {
				Node data = filmData.item(j);
				if (data.getNodeName().equals("t")) {
					title = data.getTextContent();
				}
				else if (data.getNodeName().equals("year")) {
					try {
						year = Integer.parseInt(data.getTextContent());
					} catch (NumberFormatException e) {
						year = 0;
					}
				}
				else if (data.getNodeName().equals("cats")) {
					NodeList catNodes = data.getChildNodes();
					for (int k = 0; k < catNodes.getLength(); ++k) {
						String genreName = catNodes.item(k).getTextContent();
						ParsedGenre g = new ParsedGenre(genreName);
						if (genreName == null || genreName.trim().isEmpty()) { // exclude genres with empty names
//							System.out.println("Bad Genre element: cat " + g);
							writer.write("Bad Genre element: cat " + g);
							writer.newLine();
							continue;
						}
//						System.out.println(catNodes.item(k).getNodeName() + " " + genreName);
						genresForThisMovie.add(g);
						if (myParsedGenres.contains(g)) {
//							System.out.println("Genre already exists: " + g);
							writer.write("Genre already exists: " + g);
							writer.newLine();
						}
						else
							myParsedGenres.add(new ParsedGenre(genreName));
					}
				}
			}
			ParsedMovie m = new ParsedMovie(title, year, director, genresForThisMovie);
			if (title == null || title.isEmpty()) {
//				System.out.println("Bad Movie element: t " + m);
				writer.write("Bad Movie element: t " + m);
				writer.newLine();
			}
			else if (year == 0) {
//				System.out.println("Bad Movie element: year " + m);
				writer.write("Bad Movie element: year " + m);
				writer.newLine();
			}
			else if (director == null || director.isEmpty()) {
//				System.out.println("Bad Movie element: director " + m);
				writer.write("Bad Movie element: director " + m);
				writer.newLine();
			}
			else if (genresForThisMovie.isEmpty()) {
//				System.out.println("Bad Movie element: cat " + m);
				writer.write("Bad Movie element: cat " + m);
				writer.newLine();
			}
			else {
				result.add(m);
			}
		}
		return result;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text content
	 * i.e for <star><name>John</name></star> returns John
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList tagsList = ele.getElementsByTagName(tagName);
		if (tagsList != null && tagsList.getLength() > 0) { // tag exists (but might be empty)
			Element el = (Element) tagsList.item(0); // grab first occurrence of tag
			if (el.getFirstChild() == null) // first tag is empty
				textVal = null;
			else
				textVal = el.getFirstChild().getNodeValue();
		}
		return textVal;
	}

	/**
	 * Calls getTextValue and returns an int value
	 */
	private int getIntValue(Element ele, String tagName) {
		try {
			return Integer.parseInt(getTextValue(ele, tagName));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Iterate through the list and print the content to console
	 */
	private void printData() {
		System.out.println("No of movies '" + myParsedMovies.size() + "'.");
		System.out.println("No of genres '" + myParsedGenres.size() + "'.");

//		Iterator<Movie> it = myMovies.iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next().toString());
//		}
//		Iterator<Genre> it2 = myGenres.iterator();
//		while (it2.hasNext()) {
//			System.out.println(it2.next().toString());
//		}
	}

	private void insertMovieData() {
		String sqlId = "mytestuser";
		String sqlPw = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Connection dbcon = null;
		PreparedStatement statement = null, in = null;
		Statement statement1 = null;
		ResultSet rs = null, rs2 = null;
		try {
			dbcon = DriverManager.getConnection(loginUrl, sqlId, sqlPw);
			String existingStarQuery = "SELECT id from movies where title = ? and year = ? and director = ?;";
			int count = 0;
			for (ParsedMovie parsedMovie : myParsedMovies) {
				statement = dbcon.prepareStatement(existingStarQuery);
				statement.setString(1, parsedMovie.getTitle());
				statement.setInt(2, parsedMovie.getYear());
				statement.setString(3, parsedMovie.getDirector());
				rs= statement.executeQuery();

				count++;
//				System.out.println("current movie = " + count);

				if (rs.next())
				{
//					System.out.println("movie already exists; skip" + count + " " + movie.getTitle());
				}
				else
				{
					String newMovieQuery = "Select max(id) as id from movies";
					statement1 = dbcon.createStatement();
					rs2 = statement1.executeQuery(newMovieQuery);
					rs2.next();
					int newId = Integer.parseInt(rs2.getString("id").substring(2)) + 1 ;
					String setId= "tt0" + newId;

					String addMovieQuery = "INSERT into movies VALUES(?,?,?,?)";
					in = dbcon.prepareStatement(addMovieQuery);
					in.setString(1, setId);
					in.setString(2, parsedMovie.getTitle());
					in.setInt(3, parsedMovie.getYear());
					in.setString(4, parsedMovie.getDirector());
					in.executeUpdate();
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try { if (dbcon != null) dbcon.close(); } catch (Exception ignored) {}
			try { if (statement != null) statement.close(); } catch (Exception ignored) {}
			try { if (in != null) in.close(); } catch (Exception ignored) {}
			try { if (statement1 != null) statement1.close(); } catch (Exception ignored) {}
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			try { if (rs2 != null) rs2.close(); } catch (Exception ignored) {}
		}
	}

	private void insertGenreData() {
		String sqlId = "mytestuser";
		String sqlPw = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Connection dbcon = null;
		PreparedStatement statement = null, in = null;
		ResultSet rs = null;
		try {
			dbcon = DriverManager.getConnection(loginUrl, sqlId, sqlPw);

			String existingGenreQuery = "SELECT id from genres where name = ?;";

			int count = 0;
			for (ParsedGenre parsedGenre : myParsedGenres) {
				statement = dbcon.prepareStatement(existingGenreQuery);
				statement.setString(1, parsedGenre.getName());
				rs = statement.executeQuery();

				count++;
//				System.out.println("current genre = " + count);

				if(rs.next())
				{
//					System.out.println("genre already exists; skip" + count + " " + genre.getName());
				}
				else
				{
					String addGenreQuery = "INSERT into genres VALUES(?,?)";
					in = dbcon.prepareStatement(addGenreQuery);
					in.setNull(1, Types.INTEGER);
					in.setString(2, parsedGenre.getName());
					in.executeUpdate();
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try { if (dbcon != null) dbcon.close(); } catch (Exception ignored) {}
			try { if (statement != null) statement.close(); } catch (Exception ignored) {}
			try { if (in != null) in.close(); } catch (Exception ignored) {}
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
		}
	}

	private void insertGenresInMoviesData() {
		String sqlId = "mytestuser";
		String sqlPw = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Connection dbcon = null;
		PreparedStatement statement = null, in = null;
		ResultSet rs = null;
		try {
			dbcon = DriverManager.getConnection(loginUrl, sqlId, sqlPw);

			String existingMovieQuery = "SELECT movies.id as movieId, genres.id as genreId from movies, genres where movies.title = ? and genres.name = ?;";

			for (ParsedMovie parsedMovie : myParsedMovies) {
				statement = dbcon.prepareStatement(existingMovieQuery);
				statement.setString(1, parsedMovie.getTitle());

				for (ParsedGenre parsedGenre : parsedMovie.getParsedGenres()) {
					statement.setString(2, parsedGenre.getName());
					rs = statement.executeQuery();

					if(rs.next()) // movie and genre exist; can link the two into genres_in_movies
					{
						String addGenreQuery = "INSERT into genres_in_movies VALUES(?,?)";
						in = dbcon.prepareStatement(addGenreQuery);
						in.setInt(1, rs.getInt("genreId"));
						in.setString(2, rs.getString("movieId"));
						in.executeUpdate();
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try { if (dbcon != null) dbcon.close(); } catch (Exception ignored) {}
			try { if (statement != null) statement.close(); } catch (Exception ignored) {}
			try { if (in != null) in.close(); } catch (Exception ignored) {}
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
		}
	}

	public static void main(String[] args) {
		//create an instance
		DomParserMovies dpe = new DomParserMovies();

		//call run example
		try {
			dpe.run("mains243.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
