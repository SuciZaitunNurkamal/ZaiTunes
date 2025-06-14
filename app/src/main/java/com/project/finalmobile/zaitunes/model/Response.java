package com.project.finalmobile.zaitunes.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// Model class untuk response dari API iTunes
public class Response {

	@SerializedName("resultCount")
	private int resultCount;

	@SerializedName("results")
	private List<ResultsItem> results;

	public int getResultCount() {
		return resultCount;
	}

	public List<ResultsItem> getResults() {
		return results;
	}
}
