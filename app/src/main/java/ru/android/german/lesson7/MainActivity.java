package ru.android.german.lesson7;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by german on 20.10.14.
 */
public class MainActivity extends Activity {
    ArrayList<String> channelsList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_layout);
        channelsList.add("http://echo.msk.ru/interview/rss-fulltext.xml");
        channelsList.add("http://stackoverflow.com/feeds/tag/android");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, channelsList);
        listView = (ListView)findViewById(R.id.channelsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = channelsList.get(i);
                Intent intent = new Intent(getBaseContext(), ChannelActivity.class);
                intent.putExtra("channel", s);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                channelsList.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void onAddClick(View view) {
        channelsList.add(((EditText)findViewById(R.id.editText)).getText().toString());
        adapter.notifyDataSetChanged();
        return;
    }
}
