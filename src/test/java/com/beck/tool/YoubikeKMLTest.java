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

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.PlacemarkOM;
import com.beck.kml.model.Point;
import com.beck.kml.source.YoubikeKML;

public class YoubikeKMLTest {
	
	@Test
	public void testSourceIsOpendata() throws Exception {
		final YoubikeKML unit = new YoubikeKML();
		final String str = unit.read(new File(this.getClass().getResource("/youbike").getPath()));
		final Collection<Placemark> list = unit.parse(str);
		assertThat(list).hasSize(365).element(0).isEqualTo(testPlacemark);
		
		final Placemark en = PlacemarkOM.newPlacemark("Xinyi Dist.", "MRT Taipei City Hall Stataion(Exit 3)-2", "The S.W. side of Road Zhongxiao East Road & Road Chung Yan.", new Point("121.567904444", "25.0408578889"));
		final Collection<Placemark> enList = unit.parse(str, Collections.singletonMap("en", "true"));
		assertThat(enList).hasSize(list.size()).element(0).isEqualTo(en);
	}

	@Test
	public void testSourceDataFromWeb() throws Exception {
		final YoubikeKML unit = new YoubikeKML();
		final Collection<Placemark> list = unit.parse(unit.read(new File(this.getClass().getResource("/test.htm").getPath())));
		
		//{"sno":1001,"sna":"\u5927\u9d6c\u83ef\u57ce","sarea":"\u65b0\u5e97\u5340","ar":"\u65b0\u5317\u5e02\u65b0\u5e97\u5340\u4e2d\u6b63\u8def700\u5df73\u865f"
		final Placemark placemark = PlacemarkOM.newPlacemark("新店區", "大鵬華城", "新北市新店區中正路700巷3號", new Point("121.53398", "24.99116"));
		assertThat(list).hasSize(3).element(0).isEqualTo(placemark);
	}
	
	@Test
	public void testLoadData_InvalidJsonInput() throws Exception {
		final Throwable e = catchThrowable(() -> YoubikeKML.loadData("non_json"));
		assertThat(e).isNotNull().isInstanceOfAny(RuntimeException.class);
		
		final Throwable e2 = catchThrowable(() -> YoubikeKML.loadData("['non_json_Object']"));
		assertThat(e2).isNotNull().isInstanceOfAny(RuntimeException.class);
		
		final Throwable e3 = catchThrowable(() -> YoubikeKML.loadData("test---"+testJsonStr));
		assertThat(e3).isNotNull().isInstanceOfAny(RuntimeException.class);
	}
	
	private final Placemark testPlacemark = PlacemarkOM.newPlacemark("信義區", "捷運市政府站(3號出口)", "忠孝東路/松仁路(東南側)", new Point("121.567904444", "25.0408578889"));
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
		final Placemark placemark2 = PlacemarkOM.newPlacemark("大安區", "捷運國父紀念館站(2號出口)", "忠孝東路四段/光復南路口(西南側)", new Point("121.55742", "25.041254"));
		
		final YoubikeKML unit = new YoubikeKML();
		assertThat(unit.parse(testJsonStr)).hasSize(2).containsExactly(testPlacemark, placemark2);
		
		assertThat(unit.getKMLName()).isNotNull();
	}
	
}
