# EmailExtractor
This project is for extracting emails from web pages.

Basic Usage Of this Email Extractor Tool


            String link = "http://www.aqualeader.com/en_US/contact/";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);
		System.out.println(set)
