package com.beck.kml.source;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

import com.beck.kml.model.Placemark;
import com.beck.kml.model.PlacemarkOM;
import com.beck.kml.model.Point;

public class PostOfficeTest {

	@Test
	public void testParse() throws Exception {
		final PostOffice unit = new PostOffice();
		final Collection<Placemark> list = unit.parse(unit.read(new File(this.getClass().getResource("/postOffice.csv").getPath())));
		
		final Placemark placemark1 = PlacemarkOM.newPlacemark("臺北市中正區", "臺北北門郵局", "忠孝西路一段120號1樓", new Point("121.512212", "25.047246"));
		assertThat(list).hasSize(6).element(0).isEqualTo(placemark1);

		assertThat(unit.getKMLName()).isNotNull();
	}

}
