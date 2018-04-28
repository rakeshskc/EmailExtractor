package com.email.test;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.email.emailExtractor.ExtractEmail;

public class TestCases extends TestCase {

	@Test
	public void testForNonSecureSite() throws IOException {
		String link = "http://www.aqualeader.com/en_US/contact/";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);
		assertEquals(true, set.contains("services@aqualeader.com"));
	}

	@Test
	public void testForSecureSite() throws IOException {
		String link = "http://www.targetproducts.com";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);
		System.out.println(set);
		// assertEquals(true, set.contains("services@aqualeader.com"));
	}

	/**
	 * Input URL http://www.goeasy.com and it should return [casl@goeasy.com,
	 * epic@easyhome.ca, test@test.com, username@example.com] result
	 * 
	 * @throws IOException
	 * 
	 */

	@Test
	public void testForListReturnResult() throws IOException {
		String link = "http://www.goeasy.com";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);
		assertEquals(true, set.contains("epic@easyhome.ca"));
	}

	@Test
	public void testForMultilineHyperTag() throws IOException {
		String link = "https://www.sonscapeinterlock.com/contact.html";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);		
		assertEquals(true, set.contains("joseph.a.galluzzo@gmail.com"));
	}
	
	//https://www.sonscapeinterlock.com/contact.html
	
}
