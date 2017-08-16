/*
 New BSD License http://www.opensource.org/licenses/bsd-license.php
 Copyright (c) 2017, Beck Yang
 All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice, this 
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this 
   list of conditions and the following disclaimer in the documentation and/or 
   other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.beck.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class YoubikeKML {
	private String areaKey = "sarea";
	private String nameKey = "sna";
	private String arKey = "ar";
	
	Document doc;
	private Map<String, Element> folderMap = new HashMap<String, Element>();
	
	/**
	 * The source json data may offer English/Chinese term,
	 * The library will output Chinese by default, it could be switched by this method. 
	 * Note: Must be call before parse(...)
	 */
	public void outputEnglish() {
		areaKey = "sareaen";
		nameKey = "snaen";
		arKey = "aren";
	}
	
	public String read(File inputFile) throws Exception {
		FileInputStream fin = new FileInputStream(inputFile);
		try {
			return extractJson(fin);
		} finally {
			fin.close();
		}
	}
	
	/**
	 * Extract required json string from source data 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static String extractJson(InputStream is) throws Exception {
		byte[] b = toByteArray(is);
		if (b.length > 100 && b[0] == 0x1f && b[1] == (byte)0x8b) { 
			//data is compressed...
			b = toByteArray(new GZIPInputStream(new ByteArrayInputStream(b)));
		}
		String s = new String(b, "UTF-8");
		if (s.length() > 0 && s.charAt(0) != '{') {
			//data source is html page
			int pos = s.indexOf("arealist='");
			if (pos != -1) {
				String s2 = s.substring(pos+10, s.indexOf("';", pos+100));
				s = URLDecoder.decode(s2, "UTF-8");
			}
		}
		return s;
	}
	
	public static byte[] toByteArray(InputStream is) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(16*1024);
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = is.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
		return out.toByteArray();
	}
	
	public void write(File outputFile) throws Exception {
		if (doc == null) {
			throw new IllegalArgumentException("parse(...) must be complete first");
		}
		Transformer tx = TransformerFactory.newInstance().newTransformer();
		tx.setOutputProperty(OutputKeys.INDENT, "yes");
		tx.transform(new DOMSource(doc), new StreamResult(outputFile));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parse(String jsonStr) throws Exception{
		Map json = parseJson(jsonStr);
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element kml = doc.createElementNS("http://earth.google.com/kml/2.2", "kml");
		doc.appendChild(kml);
		Element root = doc.createElement("Document");
		kml.appendChild(root);
		root.appendChild(newEleWithText("name", "YouBike bicycle-sharing stations"));
		root.appendChild(newEleWithText("description", "Created by YoutubeKML (https://github.com/BeckYang/YoubikeKML)"));
		
		ArrayList list = new ArrayList(json.keySet());
		Collections.sort(list);
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			Map po = (Map)json.get(iter.next());
			String area = String.valueOf(po.get(areaKey));
			Element folder = folderMap.get(area);
			if (folder == null) {
				folder = doc.createElement("Folder");
				folderMap.put(area, folder);
				root.appendChild(folder);
				folder.appendChild(newEleWithText("name", area));
			}
			String ar = String.valueOf(po.get(arKey));
			Element placemark = doc.createElement("Placemark");
			folder.appendChild(placemark);
			placemark.appendChild(newEleWithText("name", String.valueOf(po.get(nameKey))));
			placemark.appendChild(newEleWithText("Snippet", ar));
			placemark.appendChild(newEleWithText("description", ar));
			Element point = doc.createElement("Point");
			placemark.appendChild(point);
			point.appendChild(newEleWithText("coordinates", po.get("lng")+","+po.get("lat")+",0.000000"));
		}
	}
	
	protected Element newEleWithText(String tagName, String text) {
		Element ele = doc.createElement(tagName);
		ele.appendChild(doc.createTextNode(text));
		return ele;
	}
	
	@SuppressWarnings("rawtypes")
	public Map parseJson(String json) throws Exception{
		Object result = null;
		String err = "...] is not json object";
		try {
			result = new JSONParser().parse(json);
		} catch (ParseException e) {
			err = "...] is not valid json string";
		}
		if (result instanceof Map) {
			Map map = (Map)result;
			if (map.size() == 2 && (result = map.get("retVal")) != null && (result instanceof Map)) {
				//if data source is open data...
				map = (Map)result;
			}
			return map;
		}
		if (json.length() > 120) {
			json = json.substring(0, 120);
		}
		throw new IllegalArgumentException("Source data ["+json+err);
	}
	
}
