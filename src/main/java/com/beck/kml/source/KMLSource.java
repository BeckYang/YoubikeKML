package com.beck.kml.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.beck.kml.model.Placemark;

public interface KMLSource {
	
	//public List<Placemark> readAndParse(File inputFile) throws Exception;
	public String getKMLName();

	public String read(File inputFile) throws Exception;
	
	public Collection<Placemark> parse(String str, Map<String, String> opt) throws Exception;
	
	@SuppressWarnings("unchecked")
	default public Collection<Placemark> parse(String str) throws Exception {
		return this.parse(str, Collections.EMPTY_MAP);
	}
	
	public static byte[] readAllBytes(final InputStream is) throws Exception {
		return unGzip(toByteArray(is));
	}
	
	public static byte[] toByteArray(final InputStream is) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(16*1024);
		final byte[] b = new byte[1024];
		int len = 0;
		while ((len = is.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
		return out.toByteArray();
	}
	
	public static byte[] unGzip(final byte[] b) throws Exception {
		if (b.length > 20 && b[0] == 0x1f && b[1] == (byte)0x8b) { 
			//data is compressed...
			return toByteArray(new GZIPInputStream(new ByteArrayInputStream(b)));
		}
		return b;
	}
	
	public static String readFully(final File inputFile, final String encoding) throws Exception {
		final byte[] ba = unGzip(Files.readAllBytes(inputFile.toPath()));
		if (ba.length > 3 && ba[0] == (byte)0xEF && ba[1] == (byte)0xBB && ba[2] == (byte)0xBF) {
			return new String(ba, 3, ba.length-3, "UTF-8");//has BOM
		}
		return new String(ba, encoding==null?"UTF-8":encoding);
	}
	
	public static Object parseJson(final String json) throws ParseException {
		return new JSONParser().parse(json);
	}
	
	public static Document parseXML(final String xml) throws Exception {
		final InputSource is = new InputSource(new StringReader(xml));
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	}
	
	public static NodeList queryByXPath(final Document doc, final String xpath) throws Exception {
		final XPathExpression exp = XPathFactory.newInstance().newXPath().compile(xpath);
		return (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
	}

	final static Pattern ptnNumber = Pattern.compile("^[0-9\\.]+$");
	final static Pattern ptnAddr = Pattern.compile("^\\d*.{2}[縣市].{1,3}[區鄉鎮市]");
	public static String parseArea(final String addr) {
		final Matcher matcher = ptnAddr.matcher(addr);
		if (matcher.find()) {
			return addr.substring(0, matcher.end());
		}
		return null;
	}
}
