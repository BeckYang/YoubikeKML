package com.beck.kml.source;

import static com.beck.kml.source.KMLSource.parseXML;
import static com.beck.kml.source.KMLSource.readFully;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.Point;

public class KML implements KMLSource {
	
	public String getKMLName() {
		return "KML";
	}

	public String read(File inputFile) throws Exception {
		return readFully(inputFile, "UTF-8");
	}
	
	@Override
	public Collection<Placemark> parse(final String str, final Map<String, String> opt) throws Exception {
		final Document doc = parseXML(str);
		final XPath xpath = XPathFactory.newInstance().newXPath();
		final XPathExpression exp = xpath.compile("//Placemark");
		final NodeList list = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		final int len = list.getLength();
		final List<Placemark> items = new ArrayList<>(len);
		final Pattern ptn = Pattern.compile(",");
		for (int i = 0; i < len; i++) {
			final Element ele = (Element)list.item(i);
			final NodeList descNodes = ele.getElementsByTagName("description");
			final String description = descNodes.getLength()>0?descNodes.item(0).getTextContent():"";
			final String name = ele.getElementsByTagName("name").item(0).getTextContent().trim();
			final String coordinate = ele.getElementsByTagName("coordinates").item(0).getTextContent().trim();
			final String[] coordinates = ptn.split(coordinate);
			final Placemark placemark = new Placemark();
			getChildElementText((Element)ele.getParentNode(), "name").ifPresent(placemark::setArea);
			placemark.setName(name);
			placemark.setDescription(description);
			placemark.setPoint(new Point(coordinates[0], coordinates[1]));
			items.add(placemark);
		}
		return items;	
	}
	
	private Optional<String> getChildElementText(final Element ele, final String name) {
		Node n = ele.getFirstChild();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && name.equals(n.getNodeName())) {
				return Optional.ofNullable(n.getTextContent());
			}
			n = n.getNextSibling();
		}
		return Optional.empty();
	}

}
