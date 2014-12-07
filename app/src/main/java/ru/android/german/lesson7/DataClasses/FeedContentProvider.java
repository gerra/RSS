package ru.android.german.lesson7.DataClasses;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import android.database.SQLException;
import java.util.HashMap;

/**
 * Created by german on 08.11.14.
 */
public class FeedContentProvider extends ContentProvider {
    /**
     * Database constants declaration
     */
    private SQLiteDatabase db;

    public SQLiteDatabase getDb() {
        return db;
    }

    private static final String DB_NAME = "DBOfFeeds";
    private static final int DB_VERSION = 1;

    // Feeds
    private static final String FEEDS_TABLE = "feeds";

    public static final String FEED_ID = "_id";
    public static final String FEED_TITLE = "title";
    public static final String FEED_LINK = "link";

    // Channels
    private static final String CHANNELS_TABLE = "channels";

    public static final String CHANNEL_ID = "_id";
    public static final String CHANNEL_TITLE = "title";

    // Feeds -> Channels
    private static final String FEEDS_CHANNELS_TABLE = "feeds_channels";

    public static final String FEED_CHANNEL_ID = "_id";
    public static final String FEED_CHANNEL_FEED_ID = "feed_id";
    public static final String FEED_CHANNEL_CHANNEL_ID = "channel_id";

    private static final String FEEDS_TABLE_CREATE = "CREATE TABLE " + FEEDS_TABLE + "("
            + FEED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FEED_TITLE + " TEXT NOT NULL, "
            + FEED_LINK + " TEXT NOT NULL);";
    private static final String CHANNELS_TABLE_CREATE = "CREATE TABLE " + CHANNELS_TABLE + "("
            + CHANNEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHANNEL_TITLE + " TEXT NOT NULL);";
    private static final String FEEDS_CHANNELS_TABLE_CREATE = "CREATE TABLE " + FEEDS_CHANNELS_TABLE + "("
            + FEED_CHANNEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FEED_CHANNEL_FEED_ID  + " INTEGER, "
            + FEED_CHANNEL_CHANNEL_ID  + " INTEGER);";

    /**
     * Provider constants
     */
    private static final String AUTHORITY = "ru.android.german.lesson7";
    private static final String FEEDS_PATH = FEEDS_TABLE;
    private static final String CHANNELS_PATH = CHANNELS_TABLE;
    private static final String FEEDS_CHANNELS_PATH = FEEDS_CHANNELS_TABLE;

    public static final Uri DATA_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY);
    public static final Uri FEEDS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FEEDS_PATH);
    public static final Uri CHANNELS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CHANNELS_PATH);
    public static final Uri FEEDS_CHANNELS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FEEDS_CHANNELS_PATH);

    private static final int FEEDS = 1;
    private static final int FEEDS_ID = 2;
    private static final int CHANNELS = 3;
    private static final int CHANNELS_ID = 4;
    private static final int FEEDS_CHANNELS = 5;
    private static final int FEEDS_CHANNELS_ID = 6;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FEEDS_PATH, FEEDS);
        uriMatcher.addURI(AUTHORITY, FEEDS_PATH + "/#", FEEDS_ID);
        uriMatcher.addURI(AUTHORITY, CHANNELS_PATH, CHANNELS);
        uriMatcher.addURI(AUTHORITY, CHANNELS_PATH + "/#", CHANNELS_ID);
        uriMatcher.addURI(AUTHORITY, FEEDS_CHANNELS_PATH, FEEDS_CHANNELS);
        uriMatcher.addURI(AUTHORITY, FEEDS_CHANNELS_PATH + "/#", FEEDS_CHANNELS_ID);
    }

    private static HashMap<String, String> FEEDS_PROJECTION_MAP;
    private static HashMap<String, String> CHANNELS_PROJECTION_MAP;
    private static HashMap<String, String> FEEDS_CHANNELS_PROJECTION_MAP;

    /**
     * Help class
     */
    private static class DataBaseHelper extends SQLiteOpenHelper {
        DataBaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FEEDS_TABLE_CREATE);
            db.execSQL(CHANNELS_TABLE_CREATE);
            db.execSQL(FEEDS_CHANNELS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + FEEDS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + CHANNELS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + FEEDS_CHANNELS_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                long rowID = db.insert(FEEDS_TABLE, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(FEEDS_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Feed inserting error: Failed insert values to " + uri);
            case CHANNELS:
                rowID = db.insert(CHANNELS_TABLE, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CHANNELS_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Channel inserting error: Failed insert values to " + uri);
            case FEEDS_CHANNELS:
                rowID = db.insert(FEEDS_CHANNELS_TABLE, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(FEEDS_CHANNELS_CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Feed and channel binding error: Failed insert values to " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case FEEDS:
                qb.setTables(FEEDS_TABLE);
                qb.setProjectionMap(FEEDS_PROJECTION_MAP);
                break;
            case FEEDS_ID:
                qb.setTables(FEEDS_TABLE);
                qb.appendWhere(FEED_ID + "=" + uri.getPathSegments().get(1));
                break;
            case CHANNELS:
                qb.setTables(CHANNELS_TABLE);
                qb.setProjectionMap(CHANNELS_PROJECTION_MAP);
                break;
            case CHANNELS_ID:
                qb.setTables(CHANNELS_TABLE);
                qb.appendWhere(CHANNEL_ID + "=" + uri.getPathSegments().get(1));
                break;
            case FEEDS_CHANNELS:
                qb.setTables(FEEDS_CHANNELS_TABLE);
                qb.setProjectionMap(FEEDS_CHANNELS_PROJECTION_MAP);
                break;
            case FEEDS_CHANNELS_ID:
                qb.setTables(FEEDS_CHANNELS_TABLE);
                qb.appendWhere(FEED_CHANNEL_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder.equals("")) {
            //sortOrder = FEED_TITLE;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                count = db.delete(FEEDS_TABLE, selection, selectionArgs);
                break;
            case FEEDS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(FEEDS_TABLE, FEED_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ")" : ""), selectionArgs);
                break;
            case CHANNELS:
                count = db.delete(CHANNELS_TABLE, selection, selectionArgs);
                break;
            case CHANNELS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(CHANNELS_TABLE, CHANNEL_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ")" : ""), selectionArgs);
                break;
            case FEEDS_CHANNELS:
                count = db.delete(FEEDS_CHANNELS_TABLE, selection, selectionArgs);
                break;
            case FEEDS_CHANNELS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(FEEDS_CHANNELS_TABLE, FEED_CHANNEL_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                count = db.update(FEEDS_TABLE, values,
                        selection, selectionArgs);
                break;
            case FEEDS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(FEEDS_TABLE, values, FEED_ID +
                        " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                return "vnd.android.cursor.dir/vnd.lesson7.feeds";
            case FEEDS_ID:
                return "vnd.android.cursor.item/vnd.lesson7.feeds";
            case CHANNELS:
                return "vnd.android.cursor.dir/vnd.lesson7.channels";
            case CHANNELS_ID:
                return "vnd.android.cursor.item/vnd.lesson7.channels";
            case FEEDS_CHANNELS:
                return "vnd.android.cursor.dir/vnd.lesson7.feeds_channels";
            case FEEDS_CHANNELS_ID:
                return "vnd.android.cursor.item/vnd.lesson7.feeds_channels";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
