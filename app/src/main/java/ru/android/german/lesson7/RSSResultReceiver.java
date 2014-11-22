package ru.android.german.lesson7;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

/**
 * Created by german on 08.11.14.
 */
public class RSSResultReceiver extends ResultReceiver {
    Context context;
    public RSSResultReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult (int resultCode, Bundle resultData) {
        // Code to process resultData here
        if (resultCode == 1) {
            Toast.makeText(context, "Check your Network Connection", Toast.LENGTH_SHORT).show();
        } else if (resultCode == 2) {
            Toast.makeText(context, "Bad url of channel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == 3) {
            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
        }
    }
}
