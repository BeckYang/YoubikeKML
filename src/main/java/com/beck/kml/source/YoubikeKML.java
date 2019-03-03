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
package com.beck.kml.source;

import static com.beck.kml.source.KMLSource.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.Point;

public class YoubikeKML implements KMLSource {
	
	public String getKMLName() {
		return "YouBike bicycle-sharing stations";
	}
	
	public String read(final File inputFile) throws Exception {
		return readFully(inputFile, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<Placemark> parse(final String jsonStr, final Map<String, String> opt) throws Exception {
		/*
		 * For open data, the source json data may offer English/Chinese term,
		 * The library will output Chinese by default, it could be switched by this method. 
		 * Note: Must be set before parse(...)
		 */
		boolean outputEnglish = "true".equals(opt.get("en"));
		
		final Map json = loadData(jsonStr);
		final String areaKey = outputEnglish?"sareaen":"sarea";
		final String nameKey = outputEnglish?"snaen":"sna";
		final String arKey = outputEnglish?"aren":"ar";
		
		final ArrayList<String> keys = new ArrayList(json.keySet());
		final ArrayList<Placemark> list = new ArrayList<>(keys.size());
		Collections.sort(keys);
		final Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			final Map po = (Map)json.get(iter.next());
			final Placemark placemark = new Placemark();
			placemark.setArea(String.valueOf(po.get(areaKey)));
			placemark.setDescription(String.valueOf(po.get(arKey)));
			placemark.setName(String.valueOf(po.get(nameKey)));
			placemark.setPoint(new Point(String.valueOf(po.get("lng")), String.valueOf(po.get("lat"))));
			list.add(placemark);
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map loadData(String json) throws Exception {
		Object result = null;
		String err = "...] is not json object";
		try {
			result = parseJson(json);
		} catch (Exception e) {
			err = "...] is not valid json string";
		}
		if (result instanceof Map) {
			Map map = (Map)result;
			if (map.size() == 2 && (result = map.get("retVal")) != null && (result instanceof Map)) {
				//if data source is open data...
				map = (Map)result;
			}
			return map;
		} else if (result != null){
			Object o1 = ((List)result).get(0);
			o1 = ((Map)o1).get("resdata");
			return (Map)o1;
		}
		if (json.length() > 120) {
			json = json.substring(0, 120);
		}
		throw new IllegalArgumentException("Source data ["+json+err);
	}

	
}
