package ru.android.german.lesson7.DataClasses;

import android.content.ContentValues;

/**
 * Created by german on 20.10.14.
 */
public class Feed {
    private String title;
    private String link;
    private int channelID;
    private int feedID;

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return title + " " + link + " " + channelID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setFeedID(int feedID) {
        this.feedID = feedID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public ContentValues getContentValuesForFeed() {
        ContentValues cv = new ContentValues();
        cv.put(FeedContentProvider.FEED_TITLE, title);
        cv.put(FeedContentProvider.FEED_LINK, link);
        //cv.put(FeedContentProvider.FEED_CHANNEL_CHANNEL_ID, channelID);
        return cv;
    }

    public ContentValues getContentValuesForBinding() {
        ContentValues cv = new ContentValues();
        cv.put(FeedContentProvider.FEED_CHANNEL_FEED_ID, feedID);
        cv.put(FeedContentProvider.FEED_CHANNEL_CHANNEL_ID, channelID);
        return cv;
    }
}
