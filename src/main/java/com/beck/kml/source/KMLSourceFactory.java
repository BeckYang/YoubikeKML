package com.beck.kml.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KMLSourceFactory {
	@SuppressWarnings("rawtypes")
	private static Map<String, Class> clzNames = new HashMap<>();
	static {
		clzNames.put("youbike", YoubikeKML.class);
		clzNames.put("post", PostOffice.class);
	}
	
	private KMLSourceFactory() {
	}
	
	@SuppressWarnings("rawtypes")
	public static Optional<KMLSource> newKMLSource(final String type) throws Exception {
		Class clz = clzNames.get(type.toLowerCase());
		if (clz == null) {
			try {
				clz = Class.forName(KMLSourceFactory.class.getPackage().getName()+"."+type);
			} catch (Throwable e) {
				try {
					clz = Class.forName(type);
				} catch (Throwable e2) {
					e2.printStackTrace();
				}
			}
		}
		if (clz == null) {
			return Optional.empty();
		} else {
			return Optional.of((KMLSource)clz.newInstance());
		}
	}
	
	public static KMLSource newDefault() {
		return new YoubikeKML();
	}
}
