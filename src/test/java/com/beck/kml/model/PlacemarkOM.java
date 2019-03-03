package com.beck.kml.model;

public class PlacemarkOM {

	public static Placemark newPlacemark() {
		return newPlacemark(TestTool.randomAlphabetAndNumber(), TestTool.randomAlphabetAndNumber(), TestTool.randomAlphabetAndNumber(), 
				new Point(TestTool.randomNumberString(), TestTool.randomNumberString()));
	}
	
	public static Placemark newPlacemark(final String area, final String name, final String description, final Point point) {
		final Placemark placemark = new Placemark();
		placemark.setArea(area);
		placemark.setName(name);
		placemark.setDescription(description);
		placemark.setPoint(point);
		return placemark;
	}

}
