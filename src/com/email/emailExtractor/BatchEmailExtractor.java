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

		long start = System.currentTimeMillis();
		List<String> urlList = Files.readAllLines(Paths.get(path));
		List<String> alreadyFetchedList = Files.readAllLines(Paths
				.get(alreadyExtracted));
		List<String> timeOutRecordsList = Files.readAllLines(Paths
				.get(timeOutRecords));
		alreadyFetchedList.addAll(timeOutRecordsList);
		Set<String> visited = new HashSet<String>();
		for (String v : alreadyFetchedList) {
			String url = v.split("\t")[0];
			visited.add(url);
		}

		BatchThreadFactory factory = new BatchThreadFactory();
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

		//
		FileWriter writer = new FileWriter(new File(outPutPath));
		FileWriter writer1 = new FileWriter(new File(timeOutRecords));
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

class BatchThreadFactory implements ThreadFactory {

	static AtomicInteger ato = new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "EmailExtractThread-" + ato.getAndIncrement());
		return t;
	}

}
