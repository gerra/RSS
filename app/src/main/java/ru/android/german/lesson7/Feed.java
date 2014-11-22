package ru.android.german.lesson7;

/**
 * Created by german on 20.10.14.
 */
public class Feed {
    private String link;
    private String details;

    public String getLink() { return link; }

    public Feed(String details, String link) {
        this.details = details;
        this.link = link;
    }

    @Override
    public String toString() {
        return details;
    }

}
