package ru.android.german.lesson7;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import ru.android.german.lesson7.DataClasses.DataManager;
import ru.android.german.lesson7.DataClasses.FeedContentProvider;

/**
 * Created by german on 20.10.14.
 */
public class MainActivity extends Activity {
    Cursor cursor; // cursor of all channels
    SimpleCursorAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_layout);
        DataManager.loadContentResolver(getContentResolver());
        int layoutID = android.R.layout.simple_list_item_1;
        String from[] = {
                FeedContentProvider.CHANNEL_TITLE,
        };
        int to[] = {
                android.R.id.text1
        };

        cursor = DataManager.getAllChannels();
        startManagingCursor(cursor);
        adapter = new SimpleCursorAdapter(this, layoutID, cursor, from, to, 0);

        listView = (ListView)findViewById(R.id.channelsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(i);
                String s = c.getString(c.getColumnIndexOrThrow(FeedContentProvider.CHANNEL_TITLE));
                Intent intent = new Intent(getBaseContext(), ChannelActivity.class);
                intent.putExtra("channel", s);
                startActivity(intent);
            }
        });
        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                channelsList.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });*/
    }

    public void onAddClick(View view) {
        String channel = ((EditText)findViewById(R.id.editText)).getText().toString();
        if (channel == null || TextUtils.isEmpty(channel)) {
            Toast.makeText(getBaseContext(), "Please, type channel name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!DataManager.checkChannelExists(channel)) {
            DataManager.addChannel(channel);
            adapter.notifyDataSetChanged();
            cursor.requery();
        } else {
            Toast.makeText(getBaseContext(), "This channel already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        cursor.close();
        super.onDestroy();
    }
}
