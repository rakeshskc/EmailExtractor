package com.email.emailExtractor;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Extractor implements Callable<Result> {

	private String link = null;

	public Extractor(String link) {
		this.link = link;
	}

	@Override
	public Result call() throws IOException {
		try {
			Thread.sleep(2);
			ExtractEmail ex = new ExtractEmail();
			Result res = new Result(this.link, ex.getEmailSet(this.link));			
			return res;
		} catch (InterruptedException e) {
			System.out.println("Intrupting....\t" + this.link);
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			System.out.println(ex);
		}

		return new Result(this.link, null);

	}

	@Override
	public String toString() {
		return "Runnable " + this.link;
	}

	public int hashCode() {
		return this.link.hashCode();
	}

	public String getLink() {
		return this.link;
	}

}