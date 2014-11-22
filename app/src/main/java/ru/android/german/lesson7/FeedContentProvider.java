package ru.android.german.lesson7;

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

    static final String DB_NAME = "DBOfFeeds";
    static final int DB_VERSION = 1;

    static final String FEEDS_TABLE = "feeds";

    static final String FEED_ID = "_id";
    static final String FEED_TITLE = "title";
    static final String FEED_LINK = "link";

    static final String DB_CREATE = "CREATE TABLE " + FEEDS_TABLE + "("
            + FEED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FEED_TITLE + " TEXT NOT NULL, "
            + FEED_LINK + " TEXT NOT NULL);";

    /**
     * Provider constants
     */
    static final String AUTHORITY = "ru.android.german.lesson7";
    static final String FEEDS_PATH = FEEDS_TABLE;

    public static final Uri FEEDS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FEEDS_PATH);

    static final int FEEDS = 1;
    static final int FEEDS_ID = 2;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "feeds", FEEDS);
        uriMatcher.addURI(AUTHORITY, "feeds/#", FEEDS_ID);
    }

    private static HashMap<String, String> FEEDS_PROJECTION_MAP;

    /**
     * Help class
     */
    private static class DataBaseHelper extends SQLiteOpenHelper {
        DataBaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + FEEDS_TABLE);
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
        long rowID = db.insert(FEEDS_TABLE, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(FEEDS_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed insert values to " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FEEDS_TABLE);
        switch (uriMatcher.match(uri)) {
            case FEEDS:
                qb.setProjectionMap(FEEDS_PROJECTION_MAP);
                break;
            case FEEDS_ID:
                qb.appendWhere(FEED_ID + "=" + uri.getPathSegments().get(1));
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
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
