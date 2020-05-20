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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomParserCast {
	List<Map<String, List<Star>>> myCast;
	Document dom;
	BufferedWriter writer;

	public DomParserCast() {
		myCast = new ArrayList<>();
	}

	public void run(String filename) throws IOException {
		String inconsistenciesFilename = "parsers/inconsistencies-" + filename + ".txt";
		writer = new BufferedWriter(new FileWriter(inconsistenciesFilename));

		parseXmlFile(filename);
		parseDocument();
        printData();
		//Insert parsed data into our database
		insertData();

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
	 * Parse through entire xml document and populate lists
	 */
	private void parseDocument() throws IOException {
		//get the root elememt
		Element root = dom.getDocumentElement();

		//get list of <dirfilms> tags
		NodeList dirfilmsTags = root.getElementsByTagName("dirfilms");
		if (dirfilmsTags == null || dirfilmsTags.getLength() == 0) {
			System.out.println("dirfilmsTags is empty/null");
			return; // empty document
		}

		for (int i = 0; i < dirfilmsTags.getLength(); ++i) {
//			System.out.println("dirfilmsTags # " + i);
			// the dirfilmsTags tag to work on
			Node dirfilmsTag = dirfilmsTags.item(i);
			Map<String, List<Star>> directorMap = new HashMap<>(); // put director's map

			String title;
			ArrayList<Star> stars;
			for (int j = 0; j < dirfilmsTag.getChildNodes().getLength(); ++j) {
				Node dataTag = dirfilmsTag.getChildNodes().item(j);
//				System.out.println("dataTag: " + dataTag.getNodeName());
				if (dataTag.getNodeName().equals("filmc")) {
					title = getMovieTitle(dataTag);
					stars = getStars(dataTag);
					directorMap.put(title, stars);
				}
			}
//			System.out.println(directorMap);
			myCast.add(directorMap);
		}
//		System.out.println(myCast);
	}

	/**
	 * Get movie title from a <filmc></filmc> node
	 */
	private String getMovieTitle(Node filmcNode) throws IOException {
		NodeList mNodes = filmcNode.getChildNodes();
		if (mNodes == null || mNodes.getLength() == 0)
			return null;

		if (mNodes.item(1) == null)
			return null;

		NodeList mNodeAttributes = mNodes.item(1).getChildNodes();
		for (int i = 0; i < mNodeAttributes.getLength(); ++i) {
			if (mNodeAttributes.item(i).getNodeName().equals("t")) {
				String title = mNodeAttributes.item(i).getTextContent();
				if (title == null || title.isEmpty()) {
//					System.out.println("Bad Movie element: t ");
					writer.write("Bad Movie element: t ");
					writer.newLine();
				}
//				System.out.println("title found: " + mNodeAttributes.item(i).getTextContent());
				else
					return title;
			}
		}
		return null;
	}

	/**
	 * Get list of stars from a <filmc></filmc> node
	 */
	private ArrayList<Star> getStars(Node filmcNode) throws IOException {
		ArrayList<Star> result = new ArrayList<>();
		NodeList mNodes = filmcNode.getChildNodes();

		for (int j = 1; j < mNodes.getLength(); ++j) {
			NodeList mNodeAttributes = mNodes.item(j).getChildNodes();
			for (int i = 0; i < mNodeAttributes.getLength(); ++i) {
				Node mDataTag = mNodeAttributes.item(i);

				if (mDataTag.getNodeName().equals("a")) {
					String starName = mDataTag.getTextContent();
					Star s = new Star(starName);
					if (starName == null || starName.isEmpty()) {
//						System.out.println("Bad Star element: a " + s);
						writer.write("Bad Star element: a " + s);
						writer.newLine();
					}
					else
						result.add(s);
				}
			}
		}
//		System.out.println(result);
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

	}

	private void insertData() {
		String sqlId = "mytestuser";
		String sqlPw = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Connection dbcon = null;
		PreparedStatement statement = null, in = null;
		ResultSet rs = null;
		try {
			dbcon = DriverManager.getConnection(loginUrl, sqlId, sqlPw);
			dbcon.setAutoCommit(false);

			String existingMovieQuery = "SELECT movies.id as movieId, stars.id as starId from movies, stars where movies.title = ? and stars.name = ?;";

			for (Map<String, List<Star>> directorMap : myCast) { // title : {stars}
				//				System.out.println(count);
				for (Map.Entry<String, List<Star>> titleStars : directorMap.entrySet()) {
					statement = dbcon.prepareStatement(existingMovieQuery);
					statement.setString(1, titleStars.getKey());

					for (Star star : titleStars.getValue()) {
						statement.setString(2, star.getName());
						rs = statement.executeQuery();

						if (rs.next()) {
							String query = "INSERT INTO stars_in_movies VALUES(?,?)";
							in = dbcon.prepareStatement(query);
							in.setString(1, rs.getString("starId"));
							in.setString(2, rs.getString("movieId"));
							in.executeUpdate();
						}
					}
					dbcon.commit();
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
		DomParserCast dpe = new DomParserCast();

		//call run example
		try {
			dpe.run("casts124.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
