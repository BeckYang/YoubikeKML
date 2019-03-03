package com.beck.kml.model;

public interface TestTool {

	public static String randomAlphabetAndNumber() {
		return Long.toString(Double.doubleToLongBits(Math.random()), Character.MAX_RADIX);
	}
	
	public static String randomNumberString() {
		return Long.toString(Double.doubleToLongBits(Math.random()), 10);
	}
	
}
