package com.email.emailExtractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.email.util.OsUtils;

public class BatchEmailExtractor {
	// https://examples.javacodegeeks.com/core-java/util/concurrent/runnablefuture/java-runnablefuture-example/
	public static void main(String args[]) throws IOException,
			InterruptedException, ExecutionException, TimeoutException {

		String path = "/root/RYECache/URL.tab";
		String outPutPath = "/root/RYECache/emailResult4.tab";
		String alreadyExtracted = "/root/RYECache/emailResult3.tab";
		String timeOutRecords = "/root/RYECache/timeOutRecords.tab";

		if (OsUtils.isWindows()) {
			path = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/EmailExtraction/ZoomInfoCanada_URLS.csv";
			outPutPath = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/EmailExtraction/emailResult1.tab";
			alreadyExtracted = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/EmailExtraction/emailResult3.tab";
			timeOutRecords = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/EmailExtraction/timeOutRecords.tab";
		}

		batchEmailExtractor(path, outPutPath, alreadyExtracted, timeOutRecords);
	}

	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath) {
	}

	/**
	 * Batch wise extraction of emails using MultiThreaded Program
	 * 
	 * @param : inputEmailFile List of email in file separate by newline
	 * @param : outputPath Output file path.
	 * @param : ignorURLPath List of URL which are igoner by Batch Email
	 *        Extractor Tool
	 * @author Rakesh Chaudhari
	 */
	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath, String ignorURLPath) throws IOException,
			InterruptedException, ExecutionException {
		batchEmailExtractor(inputEmailFile, outputPath, ignorURLPath,
				ignorURLPath);
	}

	public static CustomThreadPoolExecutor pool = null;
	
	public static void batchEmailExtractor(String inputEmailFile,
			String outputPath, String ignorURLPath, String timeOutURLListPath)
			throws IOException, InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		OutPutWriter writer = new OutPutWriter(outputPath);
		FileWriter writer1 = new FileWriter(new File(timeOutURLListPath), true);

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

		ConcurrentMap<MyFutureTask<Result>, String> allTaskMap = new ConcurrentHashMap<MyFutureTask<Result>, String>();

		// Thread Factory
		BThreadFactory threadFactory = new BThreadFactory();
		BlockingQueue<Runnable> blocking = new LinkedBlockingQueue<Runnable>(
				400);		
		pool = new CustomThreadPoolExecutor(10, 75, 50000,
				TimeUnit.MILLISECONDS, blocking);

		pool.addFileHandler(writer);
		pool.setRejectedExecutionHandler(new RejectedThreadHandler());
		pool.setThreadFactory(threadFactory);
		pool.addCancelHandler(allTaskMap);

		// Monitoring Hook
		MonitorigThread monitor = new MonitorigThread(
				CustomThreadPoolExecutor.submittedTask,pool);
		monitor.start();

		// ExecutorService service = Executors.newFixedThreadPool(75, factory);
		ExecutorService service = pool;		
		int count = 0;
		Set<String> set = new HashSet<String>();
		int visitedCount = 0;
		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);

			if (!url.contains("http")) {
				url = "http://" + url;
			}

			if (visited.contains(url)) {
				visitedCount++;
				continue;
			}
			if (!set.contains(url)) {
				set.add(url);
			} else {
				continue;
			}
			Extractor extractor = new Extractor(url);
			MyFutureTask<Result> futureTask = new MyFutureTask<Result>(
					extractor);
			allTaskMap.put(futureTask, url);
			service.submit(extractor);

			count++;
		}

		service.shutdown();
		service.awaitTermination(2, TimeUnit.HOURS);
		System.err.println("Main Thread Stopped" + "\t" + visitedCount);
		monitor.cancel();
		Thread.sleep(1000);
		long end = System.currentTimeMillis();
		System.out.println("Total Latency: \t" + (end - start));
		writer1.close();
		writer.close();
	}
}