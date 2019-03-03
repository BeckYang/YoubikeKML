package com.beck.kml.source;

import static com.beck.kml.source.KMLSource.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.Point;

public class TbankAtm implements KMLSource {
	
	public String getKMLName() {
		return "T?????? bank ATM locations";
	}

	public String read(final File inputFile) throws Exception {
		return readFully(inputFile, null);
	}
	
	@Override
	public Collection<Placemark> parse(final String str, final Map<String, String> opt) throws Exception {
		final Document doc = Jsoup.parse(str);
		final Elements trs = doc.select(".table01").get(0).getElementsByTag("tr");
		final List<Placemark> items = new ArrayList<>();
		trs.stream().skip(1).forEach((ele) -> {
			final Elements tds = ele.getElementsByTag("td");
			final Element td = tds.get(1);
			findPoint(td.getElementsByTag("a").get(0).attr("href")).ifPresent(point -> {
				final String addr = td.textNodes().get(0).text().trim();
				final String name = tds.get(0).text();
				final Placemark placemark = new Placemark();
				placemark.setArea(parseArea(addr));
				placemark.setName(name);
				placemark.setDescription(addr);
				placemark.setPoint(point);
				items.add(placemark);
			});
		});
		return items;
	}
	
	private static final Pattern ptnPoint = Pattern.compile("&q=(\\d+\\.\\d+),\\D?(\\d+\\.\\d+)");
	public static Optional<Point> findPoint(final String s) {
		final Matcher matcher = ptnPoint.matcher(s);
		if (matcher.find()) {
			return Optional.of(new Point(matcher.group(2), matcher.group(1)));
		}
		return Optional.empty();
	}
}
