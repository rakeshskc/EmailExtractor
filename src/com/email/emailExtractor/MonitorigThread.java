package com.email.emailExtractor;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MonitorigThread implements Runnable {

	static ExecutorService service = Executors.newSingleThreadExecutor();
	private volatile boolean isCancel;
	public static long logRunningTimeOut = 1000 * 60 * 8;
	private ConcurrentMap<Thread, Long> map;
	private CustomThreadPoolExecutor pool;

	public MonitorigThread(ConcurrentMap<Thread, Long> map) {
		this.map = map;
	}

	public MonitorigThread(ConcurrentMap<Thread, Long> map,
			CustomThreadPoolExecutor pool) {
		this.map = map;
		this.pool = pool;
	}

	@Override
	public void run() {
		isCancel = true;
		while (isCancel) {

			try {
				Thread.sleep(1000);
				// System.out.println("RUNNNNNNNNNNNNNNNN"
				// + BatchEmailExtractor.pool.getCompletedTaskCount()
				// + "\t" + map.size() + "\t"
				// + CustomThreadPoolExecutor.activeTasks.size());
				// System.out.println(this.pool.getActiveCount() + "\t"
				// + this.pool.getQueue() + "\t"
				// + CustomThreadPoolExecutor.activeTasks);
				for (Entry<Thread, Long> entry : map.entrySet()) {
					long end = System.currentTimeMillis();
					long t = (end - entry.getValue());
					if (t >= (logRunningTimeOut)) {
						entry.getKey().interrupt();
						map.remove(entry.getKey());
					}
				}
			} catch (InterruptedException e) {
			} catch (Exception ex) {
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
