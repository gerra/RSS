package ru.android.german.lesson7;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by german on 10.11.14.
 */
public class MyHandler extends DefaultHandler {
    ContentResolver contentResolver;

    int type;

    boolean inItem;
    boolean inTitle;
    boolean inLink;
    ContentValues cv;

    String curString;
    boolean saveText = false;

    public MyHandler(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        saveText = false;
        if (qName.equals("item") || qName.equals("entry")) {
                inItem = true;
                cv = new ContentValues();
                if (qName.equals("item")) {
                    type = 1;
                } else {
                    type = 2;
                }
        } else if (inItem && qName.equals("title")) {
            inTitle = true;
            curString = new String();
            saveText = true;
        } else if (inItem && qName.equals("link")) {
            inLink = true;
            curString = new String();
            saveText = true;
            if (type == 2) {
                cv.put(FeedContentProvider.FEED_LINK, attributes.getValue("href"));
                saveText = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item") || qName.equals("entry")) {
            Uri _uri = contentResolver.insert(FeedContentProvider.FEEDS_CONTENT_URI, cv);
//            System.out.println(_uri.toString());
            inItem = false;
        } else if (qName.equals("title") && inTitle) {
            curString = curString.trim();
            cv.put(FeedContentProvider.FEED_TITLE, curString);
            inTitle = false;
        } else if (qName.equals("link") && type == 1 && inLink) {
            curString = curString.trim();
            cv.put(FeedContentProvider.FEED_LINK, curString);
            inLink = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tmp = new String(ch, start, length);
        if (saveText) {
//            System.out.println(tmp);
            curString += tmp;
        }
    }
}
