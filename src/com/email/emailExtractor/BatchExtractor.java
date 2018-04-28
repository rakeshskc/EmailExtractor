package com.email.emailExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchExtractor {

	public static ExecutorService service = Executors.newFixedThreadPool(100);
	public static AtomicInteger successCount = new AtomicInteger();

	// public static
	public static void main(String args[]) throws IOException,
			InterruptedException {

		long start = System.currentTimeMillis();
		String path = "/root/Rakesh/URL.tab";
		String outPutPath = "/root/Rakesh/emailResult1.tab";
		path = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/URL.tab";
		outPutPath = "E:/ShatamBI/Rye_Delivery/RyeWorkingDirectory/emailResult12.tab";
		List<String> urlList = Files.readAllLines(Paths.get(path));
		List<Future<String>> resutlSet = new ArrayList<Future<String>>();

		for (int i = 0; i < urlList.size(); i++) {

			if (i < 100) {
				// continue;
			}

			String url = urlList.get(i);

			if (!url.contains("http")) {
				url = "http://" + url;
			}
			if (i == 200)
				break;

			DelegateThread extractor = null;
			extractor = new DelegateThread(url, 0);
			Future<String> future = service.submit(extractor);
		}

		service.shutdown();
		while (!service.isTerminated()) {
		}
		System.out.println("Main Service");
		DelegateThread.service1.shutdownNow();

		DelegateThread.service1.shutdown();

		while (!DelegateThread.service1.isTerminated()) {

			if (DelegateThread.service1 instanceof ThreadPoolExecutor) {
				// System.out.println("Pool size is now "
				// + ((ThreadPoolExecutor) DelegateThread.service1)
				// .getActiveCount());
				ThreadPoolExecutor pool = ((ThreadPoolExecutor) DelegateThread.service1);
				pool.purge();
			}

		}
		System.out.println("All Are Executed");
		System.out.println("Found Count: " + successCount);
		long end = System.currentTimeMillis();
		System.out.println("Latency: " + (end - start));
	}
}

class DelegateThread implements Callable<String> {

	private String url;
	private long timeout;

	public static ExecutorService service1 = Executors.newFixedThreadPool(100);

	public DelegateThread(String url, long timeout) {
		this.url = url;
		this.timeout = timeout;
	}

	@Override
	public String call() throws Exception {
		WorkerThread worker = null;
		TimeCheckThread timer = null;
		RunnuingStatus runChecker = new RunnuingStatus(true);
		Future<String> future = null;
		while (runChecker.isRunning()) {
			if (worker == null) {
				worker = new WorkerThread(this.url, this.timeout);
				future = (Future<String>) service1.submit(worker);
			}
			if (worker != null) {

				String rs = worker.getResult();
				if (rs != null) {
					runChecker.setRunning(false);
					worker.stopThread();
				}

			}

			if (timer == null) {
				timer = new TimeCheckThread(this.timeout, runChecker, future);
				service1.execute(timer);
			}

			Thread.sleep(50);
			//
		}
		worker.stopThread();
		return null;
	}
}

class RunnuingStatus {

	private volatile boolean running = true;

	public RunnuingStatus(boolean running) {
		this.running = running;
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
}

class TimeCheckThread implements Runnable {

	private long timeout;
	private RunnuingStatus running;
	private Future<String> future;

	public TimeCheckThread(long timeout, RunnuingStatus running,
			Future<String> future) {
		this.timeout = timeout;
		this.running = running;
		this.future = future;
	}

	public void run() {
		try {
			Thread.sleep(1000 * 60 * 2);
			running.setRunning(false);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			running.setRunning(false);
			this.future.cancel(true);
			return;
		}
		this.future.cancel(true);
		running.setRunning(false);
	}
}
