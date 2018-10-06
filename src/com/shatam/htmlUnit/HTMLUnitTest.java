package com.shatam.htmlUnit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class HTMLUnitTest {

	public static void main(String[] args) throws InterruptedException,
			FailingHttpStatusCodeException, MalformedURLException, IOException {

		WebDriver driver = new HtmlUnitDriver();

		// Navigate to Google
		driver.get("https://www.hockeystick.co/search/W3sidW5pcXVlS2V5IjoiRW50aXR5OjpCdXNpbmVzcyIsImFzc29jaWF0ZV9mYWN0Ijp7ImNoaWxkcmVuT2YiOiJCdXNpbmVzc2VzOjpJbmNvcnBvcmF0ZWQifX1d");

		String html = driver.getPageSource();
		System.out.println(html);

		FileUtils.writeStringToFile(new File("D:/delete_file.txt"), html);

		// // Locate the searchbox using its name
		// List<WebElement> element = driver.findElements(By.tagName("form"));
		// element.get(1).findElements(By.cssSelector("input")).get(0)
		// .sendKeys("Jalgon");
		// element.get(1).findElements(By.cssSelector("input")).get(1).submit();
		// ;

		// Enter a search query
		// element.sendKeys("Jalgon");

		// Submit the query. Webdriver searches for the form using the text
		// input element automatically
		// No need to locate/find the submit button

		// element.submit();
		// Thread.sleep(3000);
		// This code will print the page title
		// System.out.println("Page title is: " + driver.getPageSource());

		// driver.quit();
	}

}
