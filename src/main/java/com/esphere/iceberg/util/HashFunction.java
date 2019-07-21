package com.esphere.iceberg.util;

public class HashFunction {

	public static long calculate(final String text) {
		return Math.abs(MurmurHash.hash64(text));
	}

}
