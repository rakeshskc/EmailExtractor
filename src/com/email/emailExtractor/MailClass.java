package com.email.emailExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MailClass {

	public static void main(String str[]) throws IOException {

		List<String> list = Files.readAllLines(Paths
				.get("E:/ShatamBI/EmailExtractor/Akama_A(1).csv"));
		long s = System.currentTimeMillis();
		list.stream().forEach(p -> System.out.println(p));
		long end = System.currentTimeMillis();
		System.out.println((end-s));
		
	}

}
