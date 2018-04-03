package com.email.emailExtractor;

import java.util.Set;

public class Result {
	private String url;
	private Set<String> set;

	public Result(String url, Set<String> set) {
		this.url = url;
		this.set = set;
	}

	public Set<String> getResultSet() {
		return this.set;
	}

	public String getLink() {
		return this.url;
	}
}
