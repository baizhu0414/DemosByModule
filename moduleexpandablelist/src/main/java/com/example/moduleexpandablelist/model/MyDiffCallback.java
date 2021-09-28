package com.example.moduleexpandablelist.model;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.library.LogUtil;

import java.util.List;

public class MyDiffCallback extends DiffUtil.Callback {

    List<User> oldList;
    List<User> newList;

    public MyDiffCallback(List<User> oldList, List<User> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    /**
     * @return 就数据集长度
     */
    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    /**
     * 是否是同一个Item
     *
     * @param oldItemPosition
     * @param newItemPosition
     * @return true相同
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);
        return oldUser.getId() == newUser.getId();
    }

    /**
     * 判断同一个Item内容是否相同
     * 注意：此处两个List需要是独立空间
     *
     * @param oldItemPosition
     * @param newItemPosition
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);
        return !TextUtils.equals(oldUser.getName(), newUser.getName())
                && oldUser.getAge() == newUser.getAge();
    }

    /**
     * 实现局部刷新
     * @param oldItemPosition
     * @param newItemPosition
     * @return 传到Adapter的onBindViewHolder的payload参数
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);

        Bundle bundle = new Bundle();
        if (!TextUtils.equals(oldUser.getName(), newUser.getName())) {
            LogUtil.e("getChangePayload:",
                    oldUser.getName() + " " + newUser.getName());
            bundle.putString("name", newUser.getName());
        }
        if (oldUser.getAge() != newUser.getAge()) {
            LogUtil.e("getChangePayload:",
                    oldUser.getAge() + " " + newUser.getAge());
            bundle.putInt("age", newUser.getAge());
        }
        return bundle;
    }
}
