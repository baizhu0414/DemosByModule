package com.example.smallfeatureuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smallfeatureuse.bean.ItemData;

import java.util.ArrayList;
import java.util.List;

public class DragListActivity extends AppCompatActivity {

    private static final String TAG = "DragListActivity";
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<ItemData> listData;
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = findViewById(R.id.drag_list);
        initData();
        initView();
    }

    private void initData() {
        listData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ItemData<String> tempTitle = new ItemData<>("content:" + i, TYPE_TITLE);
            listData.add(tempTitle);
            for (int j = 0; j < i; j++) {
                ItemData<Integer> imgTemp = new ItemData<>(i, TYPE_IMAGE);
                listData.add(imgTemp);
            }
        }
        adapter = new MyAdapter(listData, this);
    }

    private void initView() {
        // spanCount:默认每行数量
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        // lineCount = spanCount/getSpanSize()
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Log.d(TAG, "position:" + position);
                switch (listData.get(position).getDataType()) {
                    case TYPE_TITLE:
                        return 3;
                    case TYPE_IMAGE:
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    //布局1
    public class Type1ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;

        public Type1ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    //布局2
    public class Type2ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivHeader;

        public Type2ViewHolder(View itemView) {
            super(itemView);
            ivHeader = itemView.findViewById(R.id.iv_header);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ItemData> listData;
        Context context;

        public MyAdapter(List<ItemData> listData, Context context) {
            this.listData = listData;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case TYPE_TITLE:
                    view = LayoutInflater.from(context).inflate(R.layout.rv_item_title, parent, false);
                    holder = new Type1ViewHolder(view);
                    break;
                case TYPE_IMAGE:
                    view = LayoutInflater.from(context).inflate(R.layout.rv_item_head, parent, false);
                    holder = new Type2ViewHolder(view);
                    break;
                default:
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "pos adapter:" + position);
            switch (getItemViewType(position)) {
                case TYPE_TITLE:
                    Type1ViewHolder holder1 = (Type1ViewHolder) holder;
                    holder1.tvTitle.setText((String) listData.get(position).getT());
                    break;
                case TYPE_IMAGE:
                    Type2ViewHolder holder2 = (Type2ViewHolder) holder;
                    holder2.ivHeader.setImageResource(R.mipmap.ic_launcher);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "total:" + listData.size());
            return listData == null ? 0 : listData.size();
        }

        @Override
        public int getItemViewType(int position) {
//            if (listData.size() < position) {
//                return -1;
//            }
            return listData.get(position).getDataType();
        }
    }
}