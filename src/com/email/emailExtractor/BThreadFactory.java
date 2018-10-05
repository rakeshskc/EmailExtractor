package com.email.emailExtractor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class BThreadFactory implements ThreadFactory {

	static AtomicInteger ato = new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "EmailExtractThread-" + ato.getAndIncrement());
		//t.setDaemon(true);
		return t;
	}

}
