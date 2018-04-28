package com.email.emailExtractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.LogFactory;
import org.apache.xalan.xsltc.compiler.sym;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.email.util.Util;

public class ExtractEmailFB {

	static {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				null);
		//
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
				.setLevel(Level.OFF);
		//
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
				.setLevel(Level.OFF);

	}
	private static Set<String> visitedLinks = new HashSet<String>();
	private String baseLink = null;
	private String redirectLink = null;
	private String uniqueId = null;

	public static void main(String args[]) throws IOException {

		String link = "http://www.aqualeader.com/en_US/contact/";
		link = "http://www.hcll.ca";
		link = "https://spolumbos.com/contact/";
		FileWriter writer = new FileWriter("D:/rest");

		List<String> urlList = Files.readAllLines(Paths
				.get("E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/newURL.tab"));
		for (String url : urlList) {
			url = "http://www.athabascaadvocate.com";
			ExtractEmailFB obj = new ExtractEmailFB();
			Set<String> set = obj.getEmailSet(url);
			System.out.println(set);
			writer.write(url + "\t" + set.toString() + "\n");
			writer.flush();
			break;
		}
		writer.close();
	}

	public String getRedirectLink() {
		return this.redirectLink;
	}

	public static void log(Object o) {
		System.out.println(o);
	}

	private String _getSource(String url) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {

		String html = getHTML(url);
		if (html != null
				&& (html.contains("302 Found</title>") || html
						.contains("<h1>Object Moved</h1>"))) {

			String link = Util.match(html, linkRgx, 1);
			if (link != null) {
				// System.out.println("Moved Link: " + link);
				this.baseLink = link;
				this.redirectLink = link;
				return _getSource(link);
			}
		}
		return html;
	}

	private String _getFBAboutLink(String link) {
		if (!link.contains("https:")) {
			link = link.replace("http:", "https:");
		}
		String fb = "https://www.facebook.com/";
		String aboutFb = "https://www.facebook.com/pg/{aboutPage}/about/?ref=page_internal";
		String newAboutFb = null;
		if (fb.length() < link.length()) {
			String index = link.substring(fb.length());
			newAboutFb = aboutFb.replace("{aboutPage}", index);
			System.out.println(newAboutFb);
		}
		return (newAboutFb != null) ? newAboutFb : link;
	}

	public Set<String> getEmailSet(String link) throws IOException {
		String html = null;
		this.baseLink = link;
		try {
			html = _getSource(link);
			// System.out.println(html);
		} catch (Exception ex) {
			// log(ex);
			return null;
		}
		Set<String> set = getAllLinks(html);
		// System.out.println(set);
		Set<String> searchLink = getBestLinks(set);
		searchLink.add(link);// Home page link
		Set<String> emailSet = new HashSet<String>();
		for (String emailLink : searchLink) {
			// log(emailLink);

			if (emailLink.contains("facebook.com")) {
				// emailLink = _getFBAboutLink(emailLink);

				String uniqueId = Util.match(emailLink, "/[0-9]{8,}");
				if (uniqueId != null) {
					emailLink = "https://www.facebook.com" + uniqueId;
					this.uniqueId = uniqueId;

				}

				String pageHtml = null;
				try {
					pageHtml = getHTML(emailLink);
				} catch (Exception ex) {
					continue;
				}
				Set<String> email = searchForEmail(pageHtml);
				emailSet.addAll(email);
			}
		}
		return emailSet;
	}

	private static String linkRgx = "<a href=['\"]{1}(.+?)['\"]{1}(.+?)?>";
	private static String urlMatcherRgx = "(https?|ftp|file|www)://[a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	public Set<String> getAllLinks(String html) {

		// log(html);
		List<String> list = Util.matchAll(html, linkRgx, 1);
		Set<String> set = new HashSet<String>();
		set.addAll(list);
		return set;

	}

	private static String pages = "contacts?|abouts?|facebook?";

	@Deprecated
	private String makeLink1(String url) {

		if (baseLink.charAt(baseLink.length() - 1) == '/'
				&& !url.contains("http")) {
			url = baseLink + url;
			return url;
		} else if (!url.contains("http")) {
			url = baseLink + "/" + url;
			return url;
		}
		return url;
	}

	private String makeLink(String url) {

		if (baseLink.charAt(baseLink.length() - 1) == '/'
				&& !url.contains("http")) {
			if (url.startsWith("/")) {
				url = url.replaceFirst("/", "");
			}
			url = baseLink + url;
			return url;
		} else if (!url.contains("http")) {
			if (url.startsWith("/")) {
				url = baseLink + url;
			} else {
				url = baseLink + "/" + url;
			}

			return url;
		}
		return url;
	}

	public Set<String> getBestLinks(Set<String> str) {
		Set<String> set = new HashSet<String>();
		for (String link : str) {
			String linkMatch = Util.match(link, pages);
			if (linkMatch != null) {
				link = makeLink(link);
				set.add(link);
			}
		}
		// set.add("http://www.aro.ca/index.php?option=com_content&view=article&id=20&Itemid=114&lang=en");
		System.out.println(set);
		return set;
	}

	private Set<String> searchForEmail(String html) {

		Set<String> set = new HashSet<String>();
		for (String emailRgx : emailRgxList) {

			List<String> match = Util.matchAll(html, emailRgx, 0);
			if (match != null) {
				set.addAll(match);
			}

		}
		// U.log(set);
		return set;
	}

	private static ArrayList<String> emailRgxList = new ArrayList<String>() {
		{
			add("\\b[a-zA-Z\\.0-9_]+@[a-zA-Z\\.]+\\.[a-zA-Z]{2,4}");
		}
	};

	private InputStream _getHTMLSecure(String link)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		URL url = new URL(link);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection con = null;
		con = (HttpsURLConnection) url.openConnection();
		String redirectLink = getRedirectURLS(link);
		if (redirectLink != null) {
			this.redirectLink = redirectLink;
		}
		con.setAllowUserInteraction(true);
		con.setRequestMethod("GET");
		con.setSSLSocketFactory(sc.getSocketFactory());
		return con.getInputStream();
	}

	private String getRedirectURL(URLConnection conn) throws IOException {

		int status = ((HttpURLConnection) conn).getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER) {

				String newUrl = conn.getHeaderField("Location");
				return newUrl;
			}
		}
		return null;
	}

	private String getRedirectURLS(String link) throws IOException {
		URL u = new URL(link);
		HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
		int status = ((HttpsURLConnection) conn).getResponseCode();

		if (status != HttpsURLConnection.HTTP_OK) {
			if (status == HttpsURLConnection.HTTP_MOVED_TEMP
					|| status == HttpsURLConnection.HTTP_MOVED_PERM
					|| status == HttpsURLConnection.HTTP_SEE_OTHER) {

				String newUrl = conn.getHeaderField("Location");
				return newUrl;
			}

		}
		InputStream is = conn.getInputStream();
		return conn.getURL().toString();
	}

	private InputStream _getHTMLNonSecure(String link) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {
		URL url = new URL(link);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
		String urlRedirect = getRedirectURL(con);
		if (urlRedirect != null) {
			// System.out.println(urlRedirect + "\t Redirected URL");
			return _getHTMLSecure(urlRedirect);
		}
		return con.getInputStream();
	}

	public String getHTML(String link) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {

		InputStream input = null;
		if (link.toLowerCase().contains("https")) {
			input = _getHTMLSecure(link);
		} else {
			input = _getHTMLNonSecure(link);
		}

		if (this.redirectLink != null) {
			if (this.uniqueId != null) {
				int i = -1;
				if ((i = this.redirectLink.lastIndexOf("/")) != -1) {
					this.redirectLink = this.redirectLink.substring(0, i);
				}
				String fb = _getFBAboutLink(this.redirectLink);
				System.out.println(fb);
				HtmlUnitDriver driver = new HtmlUnitDriver();
				driver.get(this.redirectLink);
				String html1 = driver.getPageSource();
				FileWriter er = new FileWriter(new File("D:/s"));
				er.write(html1);
				er.close();
				return html1;
			}
		}

		byte[] arr = new byte[2024];
		int len = 0;
		StringBuffer buff = new StringBuffer();
		while ((len = input.read(arr)) != -1) {
			String str = new String(arr, 0, len);
			buff.append(str);
		}
		String html = Util.removeHtmlComments(buff.toString());
		// System.out.println(html);
		return html;
	}
}

// https://www.facebook.com/pages/The-Athabasca-Advocate/122608544476186

// http://www.comark.ca/ Note for this link pattern.
// link = "http://www.aro.ca"; > Use HTML unit driver
// link = "http://blueridgedental.ca/"; This URL is returning garbage 