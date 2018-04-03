package com.email.emailExtractor;

import java.io.IOException;
import java.util.Set;

public class WorkerThread extends Thread {

	private long timeOut;
	private String url;
	private volatile boolean running = true;
	private String result = null;

	public WorkerThread(String url, long timeOut) {
		this.timeOut = timeOut;
		this.url = url;
	}

	public static void main(String args[]) throws Exception {

		WorkerThread d = new WorkerThread("http://www.reddeermotors.com/", 12);
		d.start();
		Thread.sleep(1000);
		d.stopThread();
	}

	public String getURL() {
		return this.url;
	}

	public void run() {

		ExtractEmail ex = null;

		while (running) {

			try {
				Thread.sleep(50);
				if (ex == null) {
					ex = new ExtractEmail();
					Set<String> set = ex.getEmailSet(this.url);
					this.result = "Success";
					if (set != null && set.size() > 0) {
						BatchExtractor.successCount.incrementAndGet();
					}
					System.out.println(set);
				}
			} catch (InterruptedException e) {
				running = false;
				Thread.currentThread().interrupt();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(this.url);
		}

	}

	public void stopThread() {
		running = false;
		interrupt();
	}

	public String getResult() {
		return result;
	}
}
