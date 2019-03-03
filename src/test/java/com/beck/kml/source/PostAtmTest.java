package com.beck.kml.source;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.PlacemarkOM;
import com.beck.kml.model.Point;

public class PostAtmTest {

	@Test
	public void testParse() throws Exception {
		final PostAtm unit = new PostAtm();
		final Collection<Placemark> list = unit.parse(unit.read(new File(this.getClass().getResource("/postAtm.csv").getPath())));
		final Placemark placemark1 = PlacemarkOM.newPlacemark("Others", "力麗企業公司彰化廠--守衛室左側", "工區七路16號", new Point("120.352799", "23.9581"));
		assertThat(list).hasSize(4).element(0).isEqualTo(placemark1);
		
		assertThat(unit.getKMLName()).isNotNull();
	}

}
