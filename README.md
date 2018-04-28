# EmailExtractor
This project is for extracting emails from web pages.

#Basic Usage Of this Email Extractor Tool


            String link = "http://www.aqualeader.com/en_US/contact/";
		ExtractEmail obj = new ExtractEmail();
		Set<String> set = obj.getEmailSet(link);
		System.out.println(set)

#Batch Wise Email Extractors using multithreaded program

	/**
	Batch wise extraction of emails using MultiThreaded Program
	    @param : inputEmailFile
	             List of email in file separate by newline
	    @param : outputPath
	             Output file path.
	    @param :  ignorURLPath
	            List of URL which are igoner by Batch Email Extractor Tool
	    @author Rakesh Chaudhari                          
	*/
	
	
BatchEmailExtractor.batchEmailExtractor(inputEmailFile,
			outputPath, ignorURLPath);
			
			
			
