import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DomParserMovies {
	List<Movie> myMovies;
	Document dom;

	public DomParserMovies() {
		//create a list to hold the Movie objects
		myMovies = new ArrayList<>();
	}

	public void run() {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each movie element and create a Movie object
		parseDocument();

		//Iterate through the list and print the data
        printData();
	}

	private void parseXmlFile() {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse("mains243.xml");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Parse through entire xml document and populate myMovies list with Movie objects
	 */
	private void parseDocument() {
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
			ArrayList<Movie> movies = null;
			for (int j = 0; j < directorfilmsTag.getChildNodes().getLength(); ++j) {
				Node directorOrFilms = directorfilmsTag.getChildNodes().item(j);
//				System.out.println("directorOrFilm: " + directorOrFilms.getNodeName());
				if (directorOrFilms.getNodeName().equals("director")) {
					director = getDirectorName(directorOrFilms);
//					System.out.println("director name found: " + director);
				}
				else if (directorOrFilms.getNodeName().equals("films")) {
					movies = getMovies(directorOrFilms, director);
				}
			}

			if (movies != null) {
				System.out.println("director: " + director + "; parsed " + movies.size() + " movies");
				myMovies.addAll(movies);
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
	private ArrayList<Movie> getMovies(Node filmsNode, String director) {
		ArrayList<Movie> result = new ArrayList<>();
		NodeList filmNodes = filmsNode.getChildNodes();
		for (int i = 0; i < filmNodes.getLength(); ++i) {
			Node filmNode = filmNodes.item(i);
			NodeList filmData = filmNode.getChildNodes();

			String title = null;
			int year = 0;

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
//				else if (data.getNodeName().equals("cats"))
//					System.out.println(data.getNodeName());
			}
			if (title == null || title.isEmpty() || year == 0) {
//				System.out.println("Inconsistent data; passing movie");
			}
			else {
				result.add(new Movie(title, year, director));
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

		System.out.println("No of movies '" + myMovies.size() + "'.");

		Iterator<Movie> it = myMovies.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	public static void main(String[] args) {
		//create an instance
		DomParserMovies dpe = new DomParserMovies();

		//call run example
		dpe.run();
	}

}
