package com.example.hang.googletranslate;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HanG on 27/3/2018.
 */

public class ConversationListAdapter extends ArrayAdapter<ListItem> {

    private ArrayList<String> title;
    private ArrayList<String> description;
    private ArrayList<ListItem> listItem;
    Context mContext;


    public ConversationListAdapter(Context context, ArrayList<ListItem> listItem){
        super(context, R.layout.con_list_item, listItem);
        this.listItem = listItem;
        this.mContext = context;
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem listItem = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.con_list_item, parent, false);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            result = convertView;
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.title.setText(listItem.getTitle());
        viewHolder.description.setText(listItem.getDescription());
        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

}
