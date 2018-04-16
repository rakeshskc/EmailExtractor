package com.email.emailExtractor;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MyFutureTask<T> extends FutureTask<T> {
	private Callable<T> myTask;
	private String link;

	public MyFutureTask(Callable<T> callable) {
		super(callable);
		this.myTask = callable;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Callable<T> getMyTask() {
		return myTask;
	}

	public int hashCode() {
		Extractor s = (Extractor) this.myTask;
		return s.getLink().hashCode();
	}

	public String getLink() {
		Extractor s = (Extractor) this.myTask;
		return s.getLink();
	}

	public String toString() {	
		Extractor s = (Extractor) this.myTask;
		return s.getLink();
	}
}
