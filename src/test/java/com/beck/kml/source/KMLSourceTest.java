package com.beck.kml.source;

import static com.beck.kml.source.KMLSource.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

public class KMLSourceTest {
	
	@Test
	public void testUnGzip() throws Exception {
		final String target = "test";
		final byte[] ba = target.getBytes();
		assertThat(unGzip(ba)).containsExactly(116, 101, 115, 116);
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (GZIPOutputStream out = new GZIPOutputStream(buffer)) {
			out.write(ba);
		}
		assertThat(unGzip(buffer.toByteArray())).containsExactly(116, 101, 115, 116);
		assertThat(readAllBytes(new ByteArrayInputStream(buffer.toByteArray()))).containsExactly(116, 101, 115, 116);
	}

	@Test
	public void testParseArea() {
		assertThat(parseArea("台北市中山區伊通街58號")).isEqualTo("台北市中山區");
		assertThat(parseArea("新竹市東區中山路1號")).isEqualTo("新竹市東區");
		assertThat(parseArea("宜蘭縣宜蘭市中山路1號")).isEqualTo("宜蘭縣宜蘭市");
		assertThat(parseArea("宜蘭縣五結鄉中山路1號")).isEqualTo("宜蘭縣五結鄉");
		assertThat(parseArea("宜蘭縣頭城鎮中山路1號")).isEqualTo("宜蘭縣頭城鎮");
		assertThat(parseArea("臺東縣太麻里鄉中山路1號")).isEqualTo("臺東縣太麻里鄉");
		assertThat(parseArea("臺東縣中山路1號")).isNull();
	}
}
