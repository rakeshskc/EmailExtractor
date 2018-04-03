package com.email.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static String match(String txt, String findPattern) {

		Matcher m = Pattern.compile(findPattern, Pattern.CASE_INSENSITIVE)
				.matcher(txt);
		while (m.find()) {
			String val = txt.substring(m.start(), m.end());
			val = val.trim();

			val = val.replaceAll("\\s+", " ").trim();
			return val;
		}
		return null;
	}// match

	public static String match(String html, String expression, int groupNum) {

		Matcher m = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
				.matcher(html);

		if (groupNum <= 0) {
			if (m.find()) {
				return m.group();
			} else {
				return null;
			}
		}

		if (m.find(groupNum)) {
			return m.group(groupNum).trim();
		} else {
			return null;
		}
	}

	public static ArrayList<String> matchAll(String html, String expression,
			int groupNum) {

		Matcher m = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
				.matcher(html);

		ArrayList<String> list = new ArrayList<String>();

		while (m.find()) {
			// Util.log(m.group(groupNum));
			list.add(m.group(groupNum).trim());
		}
		return list;

	}

	public static String toString(int[] arr) {

		String s = "";
		for (int i : arr) {
			if (s.length() > 1)
				s += ",";
			s += i;
		}
		return s;
	}

	public static void log(Object o) {

		System.out.println("" + o);
	}

	static List<String> tokenize(String in) {

		return Arrays.asList(in.split("\\s+"));

	}

	static List<String> tokenize1(String in) {

		return Arrays.asList(in.split("\\s+"));

	}

	public static <T> void removeIf(final List<T> list,
			final RemoveElementPredicate<T> predicate) {
		final List<T> newList = new ArrayList<>(list.size());

		for (final T element : list) {
			if (!predicate.remove(element)) {
				newList.add(element);
			}
		}
		list.clear();
		list.addAll(newList);
	}

	public static interface RemoveElementPredicate<T> {
		public boolean remove(T t);
	}

	public static String removeHtmlComments(String str) {

		str = str.replaceAll("<!.+?-->", "");
		return str;
	}

}
