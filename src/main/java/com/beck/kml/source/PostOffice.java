package com.beck.kml.source;

import static com.beck.kml.source.KMLSource.readFully;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.Point;

public class PostOffice implements KMLSource {
	public String getKMLName() {
		//https://data.gov.tw/dataset/5950
		//https://www.post.gov.tw/post/internet/Templates/getOpenDataFile.jsp?vkey=B484EB27-B3F0-4D02-8F62-047D20C64078
		return "Post office locations";
	}

	public String read(File inputFile) throws Exception {
		return readFully(inputFile, null);
	}
	
	@Override
	public Collection<Placemark> parse(final String str, final Map<String, String> opt) throws Exception {
		final Pattern ptn = Pattern.compile(",");
		final List<Placemark> items = Stream.of(str.split("[\r\n]+")).skip(1).map(s -> {
			final String[] sa = ptn.split(s);
			final Placemark placemark = new Placemark();
			placemark.setArea(sa[8]+sa[9]);
			placemark.setName(sa[3]);
			placemark.setDescription(sa[10]);
			placemark.setPoint(new Point(sa[11], sa[12]));
			return placemark;
		}).collect(Collectors.toList());
		return items;
	}

}
