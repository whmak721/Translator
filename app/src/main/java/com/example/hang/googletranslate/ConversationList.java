package com.example.hang.googletranslate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Conversation History");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ArrayList<String> title = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        ArrayList<ArrayList<ChatMessage>> messages = new ArrayList<>();
        final ArrayList<ListItem> listItems = new ArrayList<>();
        Gson gson = new Gson();
        int i = 0;

        final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor mEditor = mPreferences.edit();
        Map<String, ?> allEntries = mPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            title.add(entry.getKey());
            ArrayList<ChatMessage> chatMessage = gson.fromJson(entry.getValue().toString(), new TypeToken<ArrayList<ChatMessage>>(){}.getType());
            messages.add(chatMessage);
        }
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            ListItem listItem = new ListItem(entry.getKey(), messages.get(i).get(0).getTranslate());
            listItems.add(listItem);
            i++;
        }

        final ListView listView = (ListView) findViewById(R.id.listView);
        final ConversationListAdapter adapter = new ConversationListAdapter(getApplicationContext(),listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String title = listItems.get(position).getTitle();
                final String description = listItems.get(position).getDescription();
                String[] item = new String[]{title, description};
                Intent intent = new Intent(ConversationList.this, MainActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    listItems.remove(position);
                                    adapter.notifyDataSetChanged();
                                    mEditor.remove(title.get(position));
                                    mEditor.apply();
                                }
                            }
                        });
        listView.setOnTouchListener(touchListener);
    }
}
