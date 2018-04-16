package com.email.emailExtractor;

import java.io.IOException;

public interface IResultWriter {
	public void write(String str) throws IOException;
}
