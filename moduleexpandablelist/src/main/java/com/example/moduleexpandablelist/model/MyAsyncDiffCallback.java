package com.example.moduleexpandablelist.model;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

/**
 * 各函数用途见：{@link MyDiffCallback}
 */
public class MyAsyncDiffCallback extends DiffUtil.ItemCallback<User> {

    @Override
    public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
        return oldItem.getAge() == newItem.getAge()
                && TextUtils.equals(oldItem.getName(), newItem.getName());
    }

    @Nullable
    @Override
    public Object getChangePayload(@NonNull User oldItem, @NonNull User newItem) {
        Bundle bundle = new Bundle();
        if (oldItem.getAge() != newItem.getAge()) {
            bundle.putInt("age", newItem.getAge());
        }
        if (!TextUtils.equals(oldItem.getName(), newItem.getName())) {
            bundle.putString("name", newItem.getName());
        }
        return bundle;
    }

}
