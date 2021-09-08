package com.example.moduleexpandablelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class MyListAdapter extends BaseAdapter {
    List<String> contents;
    boolean isListExpand = true;
    Context context;

    public MyListAdapter(List<String> listContents, Context context) {
        this.contents = listContents;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(this.context)
                .inflate(R.layout.title_item_layout, null);
        ((TextView) view.findViewById(R.id.tv_title_item)).setText(contents.get(position));
        return view;
    }
}
