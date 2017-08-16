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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class YoubikeKMLTest {
	
	@Test
	public void testGzip() throws Exception {
		YoubikeKML youbikeKML = new YoubikeKML();
		String str = YoubikeKML.extractJson(getClass().getResourceAsStream("/youbike"));
		assertTrue(str.length() > 0);
		assertNotEquals(str.indexOf("Xinyi Dist."), -1);
		assertNotEquals(str.indexOf("Taipei City Hall"), -1);
		assertTrue(youbikeKML.parseJson(str).size() > 10);
	}

	@Test
	public void testWebPage() throws Exception {
		YoubikeKML youbikeKML = new YoubikeKML();
		String str = YoubikeKML.extractJson(getClass().getResourceAsStream("/test.htm"));
		assertTrue(str.length() > 0);
		assertTrue(youbikeKML.parseJson(str).size() > 10);
	}
	
	private String testJsonStr = "{\"0001\":{\"sno\":\"0001\",\"sna\":\"捷運市政府站(3號出口)\",\"tot\":\"180\",\"sbi\":\"44\","
			+ "\"sarea\":\"信義區\",\"mday\":\"20170814113945\",\"lat\":\"25.0408578889\",\"lng\":\"121.567904444\","
			+ "\"ar\":\"忠孝東路\\/松仁路(東南側)\",\"sareaen\":\"Xinyi Dist.\",\"snaen\":\"MRT Taipei City Hall Stataion(Exit 3)-2\","
			+ "\"aren\":\"The S.W. side of Road Zhongxiao East Road & Road Chung Yan.\",\"bemp\":\"136\",\"act\":\"1\"},"
			+ "\"0002\":{\"sno\":\"0002\",\"sna\":\"捷運國父紀念館站(2號出口)\",\"tot\":\"48\",\"sbi\":\"7\","
			+ "\"sarea\":\"大安區\",\"mday\":\"20170814113935\",\"lat\":\"25.041254\",\"lng\":\"121.55742\","
			+ "\"ar\":\"忠孝東路四段\\/光復南路口(西南側)\",\"sareaen\":\"Daan Dist.\",\"snaen\":\"MRT S.Y.S Memorial Hall Stataion(Exit 2.)\","
			+ "\"aren\":\"Sec,4. Zhongxiao E.Rd\\/GuangFu S. Rd\",\"bemp\":\"41\",\"act\":\"1\"}}";
	
	@Test
	public void testParse() throws Exception {
		YoubikeKML youbikeKML = new YoubikeKML();
		youbikeKML.parse(testJsonStr);
		Element kml = youbikeKML.doc.getDocumentElement();
		assertEquals("http://earth.google.com/kml/2.2", kml.getNamespaceURI());
		Element root = findElement(kml.getFirstChild(), "Document");
		assertNotNull("<Document> does not exist", root);
		Element folder1 = findElement(root.getFirstChild(), "Folder");
		assertNotNull("1st <Folder> not exist", folder1);
		assertEquals("信義區", findElement(folder1.getFirstChild(), "name").getFirstChild().getNodeValue());
		Element p1 = findElement(folder1.getFirstChild(), "Placemark");
		assertEquals("捷運市政府站(3號出口)", findElement(p1.getFirstChild(), "name").getFirstChild().getNodeValue());
		assertEquals("忠孝東路/松仁路(東南側)", findElement(p1.getFirstChild(), "Snippet").getFirstChild().getNodeValue());
		
		Element folder2 = findElement(folder1.getNextSibling(), "Folder");
		assertNotNull("2nd <Folder> not exist", folder2);
		assertEquals("大安區", findElement(folder2.getFirstChild(), "name").getFirstChild().getNodeValue());
		
		assertNull("Expect 2 folders", folder2.getNextSibling());
	}
	
	@Test
	public void testEnglish() throws Exception {
		YoubikeKML youbikeKML = new YoubikeKML();
		youbikeKML.outputEnglish();
		youbikeKML.parse(testJsonStr);
		Element root = findElement(youbikeKML.doc.getDocumentElement().getFirstChild(), "Document");
		Element folder1 = findElement(root.getFirstChild(), "Folder");
		assertNotNull("1st folder not exist", folder1);
		assertEquals("Xinyi Dist.", findElement(folder1.getFirstChild(), "name").getFirstChild().getNodeValue());
		Element p1 = findElement(folder1.getFirstChild(), "Placemark");
		assertNull("only 1 <Placemark>", findElement(p1.getNextSibling(), "Placemark"));
		assertEquals("MRT Taipei City Hall Stataion(Exit 3)-2", findElement(p1.getFirstChild(), "name").getFirstChild().getNodeValue());
		assertEquals("The S.W. side of Road Zhongxiao East Road & Road Chung Yan.", findElement(p1.getFirstChild(), "Snippet").getFirstChild().getNodeValue());
		Element Point = findElement(p1.getFirstChild(), "Point");
		assertNotNull("<Point> not exist", Point);
		assertNotNull("<coordinates> not exist", findElement(Point.getFirstChild(), "coordinates"));
		
		Element folder2 = findElement(folder1.getNextSibling(), "Folder");
		assertEquals("Daan Dist.", findElement(folder2.getFirstChild(), "name").getFirstChild().getNodeValue());
		Element p2 = findElement(folder2.getFirstChild(), "Placemark");
		assertNull("only 1 Placemark", findElement(p2.getNextSibling(), "Placemark"));
		assertEquals("Sec,4. Zhongxiao E.Rd/GuangFu S. Rd", findElement(p2.getFirstChild(), "description").getFirstChild().getNodeValue());
	}
	
	private Element findElement(Node n, String name) {
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && name.equals(n.getNodeName())) {
				return (Element)n;
			} else {
				n = n.getNextSibling();
			}
		}
		return null;
	}
	
	
	@Test
	public void testWrite() throws Exception {
		YoubikeKML youbikeKML = new YoubikeKML();
		File tmpFile = File.createTempFile("test", "kml");
		try {
			youbikeKML.write(tmpFile);
			fail("Exception should throw");
		} catch (Throwable e) {
		}
		//youbikeKML.read(inputFile)
		
	}

}
