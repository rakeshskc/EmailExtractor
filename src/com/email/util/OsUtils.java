package com.email.util;

public class OsUtils {

	private static String OS = null;

	public static String getOsName() {
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	public static void main(String args[]) {
		System.out.println(isWindows());
	}
}
