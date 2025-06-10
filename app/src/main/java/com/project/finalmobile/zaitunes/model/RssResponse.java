package com.project.finalmobile.zaitunes.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RssResponse {
    @SerializedName("results")
    private List<ResultsItem> results;

    public List<ResultsItem> getResults() {
        return results;
    }
}