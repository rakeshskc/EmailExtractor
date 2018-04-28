package com.shatam.htmlUnit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.email.util.Util;

public class HTMLUnitDriverFB {
	// static Logger logger = Logger.getLogger(c..class);
	public static void main(String args[]) {

		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				null);
		//
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
				.setLevel(Level.OFF);
		//
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
				.setLevel(Level.OFF);

		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get("https://www.facebook.com/pg/Shatamtech/about/?ref=page_internal");
		String html = driver.getPageSource();
		String email = Util.match(html, "shatamtech@gmail.com");
		System.out.println(email);

		// // Logger logger =
		// // Logger.getLogger("c.g.htmlunit.DefaultCssErrorHandler");
		// // String log4jConfigFile = System.getProperty("user.dir")
		// // + File.separator + "log4j.properties";
		// // System.out.println(log4jConfigFile);
		// // PropertyConfigurator.configure(log4jConfigFile);
		// Logger logger = Logger.getLogger("");
		// logger.setLevel(Level.OFF);
		// // Navigate to Google
		// driver.get("https://www.facebook.com/pg/Shatamtech/about/?ref=page_internal");
		// String html = driver.getPageSource();
		// String email = Util.match(html, "shatamtech@gmail.com");
		// System.out.println(email);

		// String str = "https://www.facebook.com/spolumbos/";
		// if (!str.contains("https:")) {
		// str = str.replace("http:", "https:");
		// }
		// String fb = "https://www.facebook.com/";
		// String aboutFb =
		// "https://www.facebook.com/pg/{aboutPage}/about/?ref=page_internal";
		// if (fb.length() < str.length()) {
		// String index = str.substring(fb.length());
		// String newAboutFb = aboutFb.replace("{aboutPage}", index);
		// System.out.println(newAboutFb);
		// }

		// final WebClient webClient = new WebClient();
		// String html = webClient
		// .getPage(
		// "https://www.facebook.com/pg/Shatamtech/about/?ref=page_internal")
		// .getWebResponse().getContentAsString();
		// String email = Util.match(html, "shatamtech@gmail.com");
		// System.out.println(email);

		// System.out.println(email);
		// webClient.close();
	}

}
