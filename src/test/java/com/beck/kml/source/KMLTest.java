package com.beck.kml.source;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.PlacemarkOM;
import com.beck.kml.model.Point;

public class KMLTest {
	private KML unit;
	
	@Before
	public void init() {
		unit = new KML();
	}

	@Test
	public void testParse() throws Exception {
		final Collection<Placemark> list = unit.parse(unit.read(new File(this.getClass().getResource("/test.kml").getPath())));
		
		assertThat(list).hasSize(76).element(0)
			.hasFieldOrPropertyWithValue("area", "智慧圖書館")
			.hasFieldOrPropertyWithValue("name", "西門智慧圖書館")
			.hasFieldOrPropertyWithValue("point", new Point("121.509079", "25.04396"));
	}
	
	@Test
	public void testEmptyArea() throws Exception {
		final Collection<Placemark> list = unit.parse("<kml><Document><Placemark>" + 
				"<name>name001</name>" + 
				"<description>desc001</description>" + 
				"<Point><coordinates>121.509079,25.04396,0.000000</coordinates></Point>" + 
				"</Placemark><Placemark>" + 
				"<name>name002</name>" + 
				"<Point><coordinates>121.60,25.05,0.000000</coordinates></Point>" + 
				"</Placemark></Document></kml>");
		
		assertThat(list).hasSize(2).containsExactly(PlacemarkOM.newPlacemark(null, "name001", "desc001", new Point("121.509079", "25.04396")),
				PlacemarkOM.newPlacemark(null, "name002", "", new Point("121.60", "25.05")));
		
		
		assertThat(unit.getKMLName()).isNotNull();
	}
}
