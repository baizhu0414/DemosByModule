package com.example.moduleexpandablelist.model;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moduleexpandablelist.R;

import java.util.List;

public class UserReListAdapter extends RecyclerView.Adapter<UserReListAdapter.UserViewHolder> {

    List<User> listUser;

    public UserReListAdapter(List<User> listUser) {
        this.listUser = listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_user_item, parent, false);
        return new UserViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.tvId.setText("msg:" + listUser.get(position).getId());
        holder.tvAge.setText("" + listUser.get(position).getAge());
        holder.tvName.setText("" + listUser.get(position).getName());
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            String name = bundle.getString("name");
            int age = bundle.getInt("age");
            if (!TextUtils.isEmpty(name)) {
                holder.tvName.setText(name);
            }
            if (age != 0) {
                holder.tvAge.setText("" + age);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvAge;
        TextView tvName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_user_msg);
            tvAge = itemView.findViewById(R.id.tv_user_age);
            tvName = itemView.findViewById(R.id.tv_user_name);
        }
    }
}
