package com.email.emailExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class OutPutWriter implements IResultWriter {

	private String filePath;
	OutputStreamWriter writer = null;

	public OutPutWriter(String path) {
		this.filePath = path;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(new File(
					filePath)), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(String str) throws IOException {
		writer.write(str);
		writer.flush();
	}

	public void close() throws IOException {
		writer.close();
	}

}
