package com.email.emailExtractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.email.util.OsUtils;

public class BatchEmailExtractor {

	public static void main(String args[]) throws IOException,
			InterruptedException, ExecutionException, TimeoutException {

		String path = "/root/RYECache/URL.tab";
		String outPutPath = "/root/RYECache/emailResult4.tab";
		String alreadyExtracted = "/root/RYECache/emailResult3.tab";
		String timeOutRecords = "/root/RYECache/timeOutRecords.tab";

		if (OsUtils.isWindows()) {
			path = "E:/ShatamBI/Rye_Delivery/10March/URL.tab";
			outPutPath = "E:/ShatamBI/Rye_Delivery/10March/emailResult1.tab";
			alreadyExtracted = "E:/ShatamBI/Rye_Delivery/10March/emailResult.tab";
			timeOutRecords = "E:/ShatamBI/Rye_Delivery/10March/timeOutRecords.tab";
		}
		
		batchEmailExtractor(path, outPutPath, alreadyExtracted, timeOutRecords);
	}

	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath) {
	}

	
	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath, String ignorURLPath) throws IOException,
			InterruptedException, ExecutionException {
		batchEmailExtractor(inputEmailFile, outputPath, ignorURLPath,
				ignorURLPath);
	}

	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath, String ignorURLPath, String timeOutURLListPath)
			throws IOException, InterruptedException, ExecutionException {

		long start = System.currentTimeMillis();

		FileWriter writer = new FileWriter(new File(outputPath));
		FileWriter writer1 = new FileWriter(new File(timeOutURLListPath),true);

		List<String> urlList = Files.readAllLines(Paths.get(inputEmailFile));
		List<String> alreadyFetchedList = Files.readAllLines(Paths
				.get(ignorURLPath));
		List<String> timeOutRecordsList = Files.readAllLines(Paths
				.get(timeOutURLListPath));
		alreadyFetchedList.addAll(timeOutRecordsList);

		Set<String> visited = new HashSet<String>();
		for (String v : alreadyFetchedList) {
			String url = v.split("\t")[0];
			visited.add(url);
		}

		// Thread Factory
		BThreadFactory factory = new BThreadFactory();
		ExecutorService service = Executors.newFixedThreadPool(75, factory);
		List<Future<Result>> resutlSet = new ArrayList<Future<Result>>();
		List<String> urlListExe = new ArrayList<String>();

		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);
			if (!url.contains("http")) {
				url = "http://" + url;
			}
			if (visited.contains(url)) {
				continue;
			}
			Extractor extractor = new Extractor(url);
			Future<Result> future = service.submit(extractor);
			resutlSet.add(future);
			urlListExe.add(url);
		}

		int successRate = 0;
		for (int i = 0; i < resutlSet.size(); i++) {

			Future<Result> future = null;
			Result res = null;
			try {
				future = resutlSet.get(i);
				res = future.get(1, TimeUnit.MINUTES);
				Set<String> set = res.getResultSet();
				if (set != null && set.size() > 0) {
					successRate++;
					writer.write(res.getLink() + "\t"
							+ res.getResultSet().toString() + "\n");
					writer.flush();
				}
				System.out.println(i + "\t" + res.getLink());
			} catch (java.util.concurrent.TimeoutException ex) {
				future.cancel(true);
				System.err.println("Timeout Exeception \t" + urlListExe.get(i));
				writer1.write(urlListExe.get(i) + "\n");
				writer1.flush();
			}
		}

		Thread.sleep(1000);
		// Stop services
		List<Runnable> list = service.shutdownNow();
		System.out.println(list);
		ThreadPoolExecutor pool = ((ThreadPoolExecutor) service);
		System.out.println(pool.isTerminated() + "\t" + pool.getActiveCount());
		BlockingQueue<Runnable> queue = pool.getQueue();
		System.out.println(pool.isTerminated() + "\t" + pool.getActiveCount()
				+ "\t" + queue.size());
		System.out.println("No of Emaild Ids Found: " + successRate);
		long end = System.currentTimeMillis();
		System.out.println("Total Latency: \t" + (end - start));
		writer.close();
		writer1.close();
		if (pool.getActiveCount() >= 0) {
			System.exit(1);
		}
	}
}
