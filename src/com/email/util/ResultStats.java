package com.email.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ResultStats {

	public static void main(String args[]) throws IOException {

		List<String> list = Files.readAllLines(Paths
				.get("D:/pscp/emailtResult41.tab"));

		FileWriter writer = new FileWriter("D:/pscp/emailResult4Out1.tab");
		for (String row : list) {

			if (row.matches("(.+?)(\\[.+?@.+?\\])")) {
				writer.write(row + "\n");
			}
			
//			if(!row.equalsIgnoreCase("Result is Null")){
//				//row = "http://communitylivingalgoma.org32";
//				System.out.println(row);
//				row = row.replaceAll("\\.([a-z]+)+[0-9]{2,}", ".$1");
//				System.out.println(row);
//				//break;
//				//writer.write(row + "\n");
//				
//			}
		}
		writer.close();
	}
}
