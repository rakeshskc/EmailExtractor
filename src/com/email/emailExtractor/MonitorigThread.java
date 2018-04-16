package com.email.emailExtractor;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MonitorigThread implements Runnable {

	static ExecutorService service = Executors.newSingleThreadExecutor();
	private volatile boolean isCancel;
	private ConcurrentMap<FutureTask<Result>, Long> map;

	public MonitorigThread(ConcurrentMap<FutureTask<Result>, Long> map) {
		this.map = map;
	}

	@Override
	public void run() {
		isCancel = true;
		while (isCancel) {

			try {
				Thread.sleep(1000);
				System.out.println("RUNNNNNNNNNNNNNNNN"
						+ BatchEmailExtractor.pool.getCompletedTaskCount()
						+ "\t" + map.size() + "\t"
						+ CustomThreadPoolExecutor.activeTasks.size());

				for (Entry<FutureTask<Result>, Long> entry : map.entrySet()) {
					long end = System.currentTimeMillis();
					long t = (end - entry.getValue());
					if (t >= (1000 * 60 * 2)) {
						entry.getKey().cancel(true);
						map.remove(entry.getKey());
					}
				}

			} catch (InterruptedException e) {
				// e.printStackTrace();
			} catch (Exception ex) {
				// ex.printStackTrace();
				// System.out.println(ex);
			}
		}
	}

	public void cancel() {
		isCancel = false;
		service.shutdownNow();
	}

	public void start() {
		service.submit(this);
	}

}
