package com.beck.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.beck.kml.model.*;
import com.beck.kml.source.KMLSource;

public class KMLWriterTest {
	private KMLWriter kmlWriter;
	private File tmpFile;

	@Before
	public void init() throws Exception {
		kmlWriter = new KMLWriter();
		tmpFile = File.createTempFile("test", "kml");
		tmpFile.deleteOnExit();
	}
	
	@Test
	public void testWrite_Typical() throws Exception {
		final Placemark placemark1 = PlacemarkOM.newPlacemark();
		final Placemark placemark2 = PlacemarkOM.newPlacemark();
		final Placemark placemark3 = PlacemarkOM.newPlacemark();
		placemark2.setArea(placemark1.getArea());
		placemark3.setArea(null);
		placemark3.setDescription("<div>test</div>");
		
		kmlWriter.out(Arrays.asList(placemark1, placemark2, placemark3, placemark1)).write(tmpFile);
		final String str = Files.readAllLines(tmpFile.toPath()).stream().collect(Collectors.joining());
		assertThat(str).contains(">"+placemark1.getArea()+"<")
			.contains(">"+placemark1.getDescription()+"<")
			.contains(">"+placemark1.getName()+"<")
			.contains(">"+placemark1.getPoint().toCoordinates()+"<")
			.contains("<![CDATA["+placemark3.getDescription()+"]]>");
		assertThat(KMLSource.queryByXPath(kmlWriter.getDocument(), "//Placemark").getLength()).isEqualTo(3);
		
		kmlWriter.out(Arrays.asList(PlacemarkOM.newPlacemark()));
		assertThat(KMLSource.queryByXPath(kmlWriter.getDocument(), "//Placemark").getLength()).isEqualTo(4);
	}
	
	@Test
	public void testWrite_allowSamePoint() throws Exception {
		kmlWriter = new KMLWriter("test", false);
		final Placemark placemark1 = PlacemarkOM.newPlacemark();
		placemark1.setArea("");
		
		kmlWriter.out(Arrays.asList(placemark1, placemark1)).write(tmpFile);
		assertThat(KMLSource.queryByXPath(kmlWriter.getDocument(), "//Placemark").getLength()).isEqualTo(2);
	}
	
	@Test
	public void testWrite_Nodata() throws Exception {
		final Throwable e = catchThrowable(() -> kmlWriter.out(Collections.emptyList()).write(tmpFile));
		assertThat(e).isNotNull().hasMessage("no placemark");
	}

}
