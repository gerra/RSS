package ru.android.german.lesson7.DataClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by german on 23.11.14.
 */
public class DataManager {
    private static ContentResolver contentResolver;

    public static void loadContentResolver(ContentResolver cv) {
        contentResolver = cv;
    }

    public static Cursor getAllFeedIDsByChannel(String channel) {
        return contentResolver.query(
                FeedContentProvider.FEEDS_CHANNELS_CONTENT_URI,
                new String[] {FeedContentProvider.FEED_CHANNEL_FEED_ID},
                FeedContentProvider.FEED_CHANNEL_CHANNEL_ID + " = ? ",
                new String[] {String.valueOf(getChannelID(channel))},
                null
        );
    }

    // Only for channels methods
    public static Cursor getAllChannels() {
        return contentResolver.query(FeedContentProvider.CHANNELS_CONTENT_URI,
                null, null, null, null);
    }

    public static boolean checkChannelExists(String channel) {
        Cursor c = contentResolver.query(
                FeedContentProvider.CHANNELS_CONTENT_URI,
                null,
                FeedContentProvider.CHANNEL_TITLE + " = ? ",
                new String[] {channel},
                null);
        int cnt = c.getCount();
        c.close();
        return cnt != 0;
    }

    public static void addChannel(String channel) {
        ContentValues cv = new ContentValues();
        cv.put(FeedContentProvider.CHANNEL_TITLE, channel);
        contentResolver.insert(FeedContentProvider.CHANNELS_CONTENT_URI, cv);
    }

    public static int getChannelID(String channel) {
        Cursor c = contentResolver.query(
                FeedContentProvider.CHANNELS_CONTENT_URI,
                new String[] {FeedContentProvider.CHANNEL_ID},
                FeedContentProvider.CHANNEL_TITLE + " = ? ",
                new String[] {channel},
                null
        );
        c.moveToPosition(0);
        int channelID = c.getInt(c.getColumnIndexOrThrow(FeedContentProvider.CHANNEL_ID));
        c.close();
        return channelID;
    }

    public static void deleteChannel(String channel) {

    }

    // Only for feeds methods
    public static void addFeed(Feed feed) {
        ContentValues cv = feed.getContentValuesForFeed();
        contentResolver.insert(FeedContentProvider.FEEDS_CONTENT_URI, cv);
        int feedID = getFeedID(feed);
        feed.setFeedID(feedID);
        cv = feed.getContentValuesForBinding();
        contentResolver.insert(FeedContentProvider.FEEDS_CHANNELS_CONTENT_URI, cv);
    }

    public static int getFeedID(Feed feed) {
        Cursor c = contentResolver.query(
                FeedContentProvider.FEEDS_CONTENT_URI,
                new String[] {FeedContentProvider.FEED_ID},
                FeedContentProvider.FEED_LINK + " = ? ",
                new String[] {feed.getLink()},
                null
        );
        c.moveToPosition(0);
        int feedID = c.getInt(c.getColumnIndexOrThrow(FeedContentProvider.FEED_ID));
        c.close();
        return feedID;
    }

    public static void deleteAllFeedsFromChannel(String channel) {
        Cursor c = getAllFeedIDsByChannel(channel);
        c.moveToPosition(0);
        int cnt = c.getCount();
        if (cnt == 0) {
            c.close();
            return;
        }
        String[] allFeeds = new String[cnt];
        String where = "";

        for (int i = 0; i < cnt; i++) {
            where += (i == 0 ?  "" : "OR ") + FeedContentProvider.FEED_ID + " = ? ";
            allFeeds[i] = String.valueOf(c.getInt(c.getColumnIndexOrThrow(FeedContentProvider.FEED_CHANNEL_FEED_ID)));
            c.moveToNext();
        }

        contentResolver.delete(
                FeedContentProvider.FEEDS_CONTENT_URI,
                where,
                allFeeds
        );
        contentResolver.delete(
                FeedContentProvider.FEEDS_CHANNELS_CONTENT_URI,
                FeedContentProvider.FEED_CHANNEL_CHANNEL_ID + " = ? ",
                new String[] {String.valueOf(getChannelID(channel))}
        );
        c.close();
    }

    public static void deleteAllFeeds() {
        contentResolver.delete(FeedContentProvider.FEEDS_CONTENT_URI, null, null);
        contentResolver.delete(FeedContentProvider.FEEDS_CHANNELS_CONTENT_URI, null, null);
    }

    public static Cursor getAllFeedsFromChannel(String channel) {
        Cursor c = getAllFeedIDsByChannel(channel);
        c.moveToPosition(0);
        int cnt = c.getCount();
        if (cnt == 0) {
            c.close();
            c = contentResolver.query(
                    FeedContentProvider.FEEDS_CONTENT_URI,
                    new String[] {
                            FeedContentProvider.FEED_ID,
                            FeedContentProvider.FEED_TITLE,
                            FeedContentProvider.FEED_LINK
                    },
                    "_id=-1", // i want to return empty cursor
                    null,
                    null
            );
            return c;
        }
        String[] allFeeds = new String[cnt];
        String where = "";

        for (int i = 0; i < cnt; i++) {
            where += (i == 0 ?  "" : "OR ") + FeedContentProvider.FEED_ID + " = ? ";
            allFeeds[i] = String.valueOf(c.getInt(c.getColumnIndexOrThrow(FeedContentProvider.FEED_CHANNEL_FEED_ID)));
            c.moveToNext();
        }
        c.close();
        c = contentResolver.query(
                FeedContentProvider.FEEDS_CONTENT_URI,
                new String[] {
                        FeedContentProvider.FEED_ID,
                        FeedContentProvider.FEED_TITLE,
                        FeedContentProvider.FEED_LINK
                },
                where,
                allFeeds,
                null
        );
        return c;
    }
}