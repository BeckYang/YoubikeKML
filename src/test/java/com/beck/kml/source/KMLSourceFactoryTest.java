package com.beck.kml.source;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class KMLSourceFactoryTest {

	@Test
	public void testNewKMLSource() throws Exception {
		assertThat(KMLSourceFactory.newKMLSource("notExist")).isNotPresent();
		assertThat(KMLSourceFactory.newKMLSource("youbike")).isPresent();
		assertThat(KMLSourceFactory.newKMLSource("post")).isPresent();
		assertThat(KMLSourceFactory.newKMLSource("TbankAtm")).isPresent();
		assertThat(KMLSourceFactory.newKMLSource("PostAtm")).isPresent();
		
		assertThat(KMLSourceFactory.newKMLSource("youbike")).isPresent()
			.hasValueSatisfying(o -> assertThat(o.getClass()).isEqualTo(YoubikeKML.class));
		assertThat(KMLSourceFactory.newDefault()).hasFieldOrPropertyWithValue("class", YoubikeKML.class);
	}
	
}
