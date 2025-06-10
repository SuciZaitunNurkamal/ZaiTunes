package com.project.finalmobile.zaitunes.model;

import com.google.gson.annotations.SerializedName;

public class RssResponse {
    @SerializedName("feed")
    private RssFeed feed;

    public RssFeed getFeed() {
        return feed;
    }
}