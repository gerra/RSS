package ru.android.german.lesson7;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.ResultReceiver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by german on 08.11.14.
 */
public class FeedLoader extends IntentService {
    public FeedLoader() {
        super("FeedLoader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        URL url;
        String urlString = intent.getStringExtra("url");
//        System.out.println("I'm in handle");
        try {
            url = new URL(urlString);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                // clearFeeds without displaying on listView
                getContentResolver().delete(FeedContentProvider.FEEDS_CONTENT_URI, null, null);
                // starting parsing
                XMLParserSax.parse(in, getContentResolver());
            } else {
                receiver.send(3, null);
            }
        } catch (MalformedURLException e) {
            receiver.send(2, null);
            e.printStackTrace();
        } catch(UnknownHostException e) {
            receiver.send(1, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
