package com.example.moduleexpandablelist.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.moduleexpandablelist.R;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    List<User> userList;

    public void setList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflate = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.rv_user_item, null);
        TextView tvName = (TextView) inflate.findViewById(R.id.tv_user_name);
        TextView tvAge = inflate.findViewById(R.id.tv_user_age);
        TextView tvMsg = inflate.findViewById(R.id.tv_user_msg);
        tvName.setText(userList.get(position).getName());
        tvAge.setText("" + userList.get(position).getAge());
        tvMsg.setText("" + userList.get(position).getId());
        return inflate;
    }
}
