package com.project.finalmobile.zaitunes.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RssFeed {
    @SerializedName("title")
    private String title;

    @SerializedName("results")
    private List<ResultsItem> results;

    public String getTitle() {
        return title;
    }

    public List<ResultsItem> getResults() {
        return results;
    }
}