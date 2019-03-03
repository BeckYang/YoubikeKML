package com.beck.tool;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.Point;

public class KMLWriter {
	final private Document doc;
	final private Element root;
	final private Element folderDef;
	final private Map<String, Element> folderMap = new HashMap<String, Element>();
	final private boolean checkSamePoint;
	
	private int totalCount = 0;
	
	public KMLWriter() throws Exception {
		this ("YouBike bicycle-sharing stations", true);
	}
	public KMLWriter(final String name, final boolean checkSamePoint) throws Exception {
		this.checkSamePoint = checkSamePoint;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		final Element kml = doc.createElementNS("http://earth.google.com/kml/2.2", "kml");
		doc.appendChild(kml);
		root = doc.createElement("Document");
		kml.appendChild(root);
		root.appendChild(newEleWithText("name", name));
		root.appendChild(newEleWithText("description", "Created by YoutubeKML (https://github.com/BeckYang/YoubikeKML)"));
		folderDef = doc.createElement("Folder");
		folderDef.appendChild(newEleWithText("name", "Default"));
	}
	
	public void write(final File outputFile) throws Exception {
		if (totalCount == 0) {
			throw new IllegalArgumentException("no placemark");
		}
		final Transformer tx = TransformerFactory.newInstance().newTransformer();
		tx.setOutputProperty(OutputKeys.INDENT, "yes");
		tx.transform(new DOMSource(doc), new StreamResult(outputFile));
	}

	public KMLWriter out(final Iterable<Placemark> list) throws Exception {
		int count = 0;
		final HashSet<Point> points = new HashSet<Point>(256);
		final Iterator<Placemark> iter = list.iterator();
		final boolean forceAdd = !checkSamePoint;
		while (iter.hasNext()) {
			final Placemark po = iter.next();
			if (forceAdd || !points.contains(po.getPoint())) {
				points.add(po.getPoint());
				final String area = po.getArea();
				Element folder = (area==null||area.length()==0)?folderDef:folderMap.get(area);
				if (folder == null) {
					folder = doc.createElement("Folder");
					folderMap.put(area, folder);
					root.appendChild(folder);
					folder.appendChild(newEleWithText("name", area));
				}
				final Element placemark = doc.createElement("Placemark");
				folder.appendChild(placemark);
				placemark.appendChild(newEleWithText("name", po.getName()));
				final String[] desc = splitHTML(po.getDescription()); 
				placemark.appendChild(newEleWithText("Snippet", desc[0]));
				placemark.appendChild(desc.length==1?newEleWithText("description", desc[0]):newEleWithCData("description", desc[1]));
				final Element point = doc.createElement("Point");
				placemark.appendChild(point);
				point.appendChild(newEleWithText("coordinates", po.getPoint().toCoordinates()));
				count++;
			}
		}
		if (folderDef.getFirstChild().getNextSibling() != null && folderDef.getParentNode() == null) {
			root.appendChild(folderDef);
		}
		totalCount = totalCount + count;
		return this;
	}
	
	protected String[] splitHTML(final String s) {
		final int pos = s.indexOf('<');
		if (pos == -1) {
			return new String[]{s};
		} else {
			return new String[]{s.substring(0, pos), s};
		}
	}
	
	protected Element newEleWithText(final String tagName, final String text) {
		final Element ele = doc.createElement(tagName);
		ele.appendChild(doc.createTextNode(text));
		return ele;
	}
	
	protected Element newEleWithCData(final String tagName, final String text) {
		final Element ele = doc.createElement(tagName);
		ele.appendChild(doc.createCDATASection(text));
		return ele;
	}
	
	Document getDocument() {
		return doc;
	}
}
