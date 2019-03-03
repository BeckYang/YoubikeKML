package com.beck.kml.source;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

import com.beck.kml.model.*;

import static org.assertj.core.api.Assertions.*;

public class TbankAtmTest {

	@Test
	public void testParse() throws Exception {
		final TbankAtm unit = new TbankAtm();
		final Collection<Placemark> list = unit.parse(unit.read(new File(this.getClass().getResource("/tbank_atm.htm").getPath())));
		
		final Placemark placemark1 = PlacemarkOM.newPlacemark("台北市中山區", "全家_金通", "台北市中山區伊通街58號", new Point("121.534633", "25.051522"));
		assertThat(list).hasSize(10).element(0).isEqualTo(placemark1);
		
		assertThat(unit.getKMLName()).isNotNull();
	}
	
	@Test
	public void testFindPoint() {
		assertThat(TbankAtm.findPoint("").isPresent()).isFalse();
	}

}
