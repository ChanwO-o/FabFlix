import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DomParserStars {
	List<Star> myStars;
	Document dom;

	public DomParserStars() {
		//create a list to hold the star objects
		myStars = new ArrayList<>();
	}

	public void run() {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each star element and create a star object
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
			dom = db.parse("actors-short.xml");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Parse through entire xml document and populate myStars list with Star objects
	 */
	private void parseDocument() {
		//get the root elememt
		Element root = dom.getDocumentElement();

		//get a nodelist of <actor> elements
		NodeList actorNodesList = root.getElementsByTagName("actor");
		if (actorNodesList != null && actorNodesList.getLength() > 0) {
			System.out.println("actorNodesList length: " + actorNodesList.getLength());

			for (int i = 0; i < actorNodesList.getLength(); i++) {

				//get the star element
				Element el = (Element) actorNodesList.item(i);

				//get the Star object
				Star s = getStar(el);

				//add it to list
				myStars.add(s);
			}
		}
	}

	/**
	 * Create a Star object out of passed xml element
	 */
	private Star getStar(Element empEl) {

		//for each <star> element get text or int values of
		//stagename, dob
		String name = getTextValue(empEl, "stagename");
		int dob = getIntValue(empEl, "dob");

//		System.out.println("name: " + name + " dob: " + dob);

		//Create a new Star with the value read from the xml nodes
		return new Star(name, dob);
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

		System.out.println("No of Employees '" + myStars.size() + "'.");

		Iterator<Star> it = myStars.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	public static void main(String[] args) {
		//create an instance
		DomParserStars dpe = new DomParserStars();

		//call run example
		dpe.run();
	}

}
