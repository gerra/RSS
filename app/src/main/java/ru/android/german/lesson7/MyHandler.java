package ru.android.german.lesson7;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ru.android.german.lesson7.DataClasses.DataManager;
import ru.android.german.lesson7.DataClasses.Feed;
import ru.android.german.lesson7.DataClasses.FeedContentProvider;

/**
 * Created by german on 10.11.14.
 */
public class MyHandler extends DefaultHandler {
    int type;

    boolean inItem;
    boolean inTitle;
    boolean inLink;

    Feed feed;

    String curString;
    boolean saveText = false;

    int channelID;

    public MyHandler(String channel) {
        channelID = DataManager.getChannelID(channel);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        saveText = false;
        if (qName.equals("item") || qName.equals("entry")) {
                inItem = true;
                feed = new Feed();
                feed.setChannelID(channelID);
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
                feed.setLink(attributes.getValue("href"));
                saveText = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item") || qName.equals("entry")) {
            DataManager.addFeed(feed);
//            System.out.println(_uri.toString());
            inItem = false;
        } else if (qName.equals("title") && inTitle) {
            curString = curString.trim();
            feed.setTitle(curString);
            inTitle = false;
        } else if (qName.equals("link") && type == 1 && inLink) {
            curString = curString.trim();
            feed.setLink(curString);
            inLink = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tmp = new String(ch, start, length);
        if (saveText) {
            curString += tmp;
        }
    }
}
