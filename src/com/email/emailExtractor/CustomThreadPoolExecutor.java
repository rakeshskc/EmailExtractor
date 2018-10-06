package com.email.emailExtractor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//https://howtodoinjava.com/core-java/multi-threading/how-to-use-blockingqueue-and-threadpoolexecutor-in-java/

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

	public static ConcurrentMap<Thread, Long> submittedTask = new ConcurrentHashMap<Thread, Long>();
	public static ConcurrentMap<MyFutureTask<Result>, String> allTaskMap = new ConcurrentHashMap<MyFutureTask<Result>, String>();
	public static final ConcurrentHashMap<Runnable, MyThread> activeTasks = new ConcurrentHashMap<>();
	IResultWriter iWriter = null;

	public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public void addFileHandler(IResultWriter iWriter) {
		this.iWriter = iWriter;
	}

	public void addCancelHandler(ConcurrentMap<MyFutureTask<Result>, String> map) {
		allTaskMap = map;
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new MyFutureTask<T>(callable);
	}

	protected void terminated() {
		super.terminated();

	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		if (r instanceof MyFutureTask) {

			MyFutureTask<Result> d = (MyFutureTask<Result>) r;
			submittedTask.put(t, System.currentTimeMillis());
			allTaskMap.put(d, d.toString());

		} else {

		}
		activeTasks.put(r, new MyThread(t, System.currentTimeMillis()));
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		System.out.println(r + "-- DONE");
		activeTasks.remove(r);
		if (t != null) {
			System.out.println("Perform exception handler logic");
		}
		FutureTask<Result> f = (FutureTask<Result>) r;
		if (f.isCancelled()) {
			if (allTaskMap != null) {
				String url = allTaskMap.get(f);
				try {
					this.iWriter.write(url + "\t" + "TIMEOUT \n");
				} catch (IOException e) {
				}
			}
			submittedTask.remove(f);
			return;
		}

		try {

			Result res = f.get();
			if (res != null && res.getResultSet() == null) {
				try {
					this.iWriter.write(res.getLink() + "\t"
							+ "Email Not Found or Site is Inactive \n");
				} catch (IOException e) {

				}
				submittedTask.remove(f);
				return;
			}

			if (res == null) {
				try {
					MyFutureTask<Result> f1 = (MyFutureTask<Result>) f;
					this.iWriter.write(f1 + "\t" + "Result is Null" + "\n");
				} catch (IOException e) {
					System.out.println(e);
				}
				return;
			}

			Set<String> set = res.getResultSet();
			if (set != null && set.size() > 0) {
				if (this.iWriter != null) {
					this.iWriter.write(res.getLink() + "\t"
							+ res.getResultSet().toString() + "\n");
				}
			}

			if (set != null && set.size() == 0) {
				this.iWriter.write(res.getLink() + "\t"
						+ res.getResultSet().toString() + "\n");
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		submittedTask.remove(f);
	}
}

class MyThread {
	private Thread t;
	private long time;
	MyFutureTask<Result> d = null;

	public MyThread(Thread t, long time) {
		this.t = t;
		this.time = time;
	}

	public MyThread(Thread t, long time, Runnable r) {
		this.t = t;
		this.time = time;
		d = (MyFutureTask<Result>) r;
	}

	public String toString() {
		return t.getName() + "\t" + d.getLink();
	}

	public long getTime() {
		return time;
	}

	public Thread getThread() {
		return this.t;
	}
}