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

public class PostAtm implements KMLSource {
	public String getKMLName() {
		//https://data.gov.tw/dataset/52784
		//https://www.post.gov.tw/post/internet/Templates/getOpenDataFile.jsp?vkey=B87D513E-A2A0-4FCA-B726-489015A956F3
		return "Post office ATM locations";
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
			placemark.setArea("Others");
			placemark.setName(sa[0]);
			placemark.setDescription(sa[1]);
			placemark.setPoint(new Point(sa[6], sa[7]));
			return placemark;
		}).collect(Collectors.toList());
		return items;
	}

}
