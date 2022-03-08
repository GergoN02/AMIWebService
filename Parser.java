package uk.ac.mmu.advprog.hackathon;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Parser {

	/**
	 * Makes JSONArray from key-value pairs
	 * 
	 * @param freqMap HashMap of String-String key value pairs of frequency
	 * @return JSONArray of key-value pair objects
	 */
	public static JSONArray frequencyToJSONArray(HashMap<String, String> freqMap) {

		JSONArray resJSON = new JSONArray();

		Iterator<Map.Entry<String, String>> hashIterator = freqMap.entrySet().iterator();

		while (hashIterator.hasNext()) {
			HashMap.Entry<String, String> keyVal = (HashMap.Entry<String, String>) hashIterator.next();

			JSONObject obj = new JSONObject();
			obj.put("Value: " + keyVal.getKey(), "Frequency: " + keyVal.getValue());

			resJSON.put(obj);
		}

		return resJSON;
	}

	/**
	 * Creates XML Document from distinctive groups of signals
	 * 
	 * @param groups Signal groups in ArrayList of Strings
	 * @return XML Document of each distinctive group in its own element
	 */
	public static Document makeXMLGroups(ArrayList<String> groups) {

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlOut = db.newDocument();

			Element root = xmlOut.createElement("Groups");
			xmlOut.appendChild(root);

			for (int i = 0; i < groups.size(); i++) {
				Element newGroup = xmlOut.createElement("Group");
				newGroup.appendChild(xmlOut.createTextNode(groups.get(i)));
				root.appendChild(newGroup);
			}

			return xmlOut;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates XML Document from an Array of Signal objects
	 * 
	 * @param signals Array of Signal objects
	 * @return XML Document with structured Signal attributes
	 */
	public static Document makeXMLSignals(ArrayList<Signal> signals) {

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlOut = db.newDocument();

			Element root = xmlOut.createElement("Signals");
			xmlOut.appendChild(root);

			for (int i = 0; i < signals.size(); i++) {
				Element signal = xmlOut.createElement("Signal");
				Element ID = xmlOut.createElement("ID");
				Element DateSet = xmlOut.createElement("DateSet");
				Element Value = xmlOut.createElement("Value");

				ID.appendChild(xmlOut.createTextNode(signals.get(i).ID));
				Value.appendChild(xmlOut.createTextNode(signals.get(i).Value));
				DateSet.appendChild(xmlOut.createTextNode(signals.get(i).DateSet));

				signal.appendChild(ID);
				signal.appendChild(DateSet);
				signal.appendChild(Value);

				root.appendChild(signal);
			}

			return xmlOut;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Takes XML Document and converts it to a String buffer
	 * 
	 * @param xml XML Document to be converted
	 * @return String of XML Document
	 */
	public static String formatDocument(Document xml) {

		TransformerFactory tf = TransformerFactory.newInstance();

		try {
			Transformer transformer = tf.newTransformer();

			StringWriter sw = new StringWriter();

			transformer.transform(new DOMSource(xml), new StreamResult(sw));

			return sw.getBuffer().toString();

		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;

	}

}
