package com.beck.kml.model;

public class Point {
	public String lng;
	public String lat;
	
	public Point() {
	}

	public Point(String lng, String lat) {
		this.lng = lng;
		this.lat = lat;
	}
	
	public String toCoordinates() {
		//"coordinates", po.get("lng")+","+po.get("lat")+",0.000000" ex: "121.567904444,25.0408578889,0.000000"
		return lng+","+lat+",0.000000";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lng == null) ? 0 : lng.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lng == null) {
			if (other.lng != null)
				return false;
		} else if (!lng.equals(other.lng))
			return false;
		return true;
	}
}
