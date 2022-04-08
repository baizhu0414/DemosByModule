package com.example.smallfeatureuse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smallfeatureuse.bean.ItemData;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv_movable;
    ItemTouchHelper mItemTouchHelper;//移动事件
    Madapter mAdapter;
    ArrayList<ItemData<TypeItemData>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_movable = findViewById(R.id.rv_movable);
        initData();
        setTouch();
    }

    void initData(){
        dataList = new ArrayList<>();
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("Title--title111"), Madapter.TYPE_ITEM_TITLE));
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("type 1"), Madapter.TYPE_ITEM_CHOOSE));
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("type 2"), Madapter.TYPE_ITEM_CHOOSE));
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("Title--title222"), Madapter.TYPE_ITEM_TITLE));
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("type 3"), Madapter.TYPE_ITEM_CHOOSE));
        dataList.add(new ItemData<TypeItemData>(new TypeItemData("type 4"), Madapter.TYPE_ITEM_CHOOSE));
        mAdapter = new Madapter(dataList);
    }

    void setTouch() {
        mItemTouchHelper = new ItemTouchHelper(callback);
        rv_movable.setLayoutManager(new LinearLayoutManager(this));
        rv_movable.setAdapter(mAdapter);
        mItemTouchHelper.attachToRecyclerView(rv_movable);
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            if (recyclerView.getAdapter() != null) {
                mAdapter.notifyItemMoved2(fromPosition, toPosition);
            }

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                return;
            }
            viewHolder.itemView.setBackgroundColor(Color.GRAY);
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            super.clearView(recyclerView, viewHolder);
        }
    };

    class Madapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM_CHOOSE = 0;//分类
        private static final int TYPE_ITEM_TITLE = 1;//标题
        private ArrayList<ItemData<TypeItemData>> dataList;//数据集合

        public Madapter(ArrayList<ItemData<TypeItemData>> dataList) {
            this.dataList = dataList;
        }

        //用来创建ViewHolder
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //如果viewType是编辑框类型,则创建EditViewHolder型viewholder
            if (viewType == TYPE_ITEM_CHOOSE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_filter, parent, false);
                return new TypeViewHolder(view);
            }
            //如果viewType是按钮类型,则创建ButtonViewHolder型viewholder
            if (viewType == TYPE_ITEM_TITLE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_title, parent, false);
                return new TitleViewHolder(view);
            }
            return null;
        }

        //用来绑定数据
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //如果holder是EditViewHolder的实例
            if (holder instanceof TypeViewHolder) {
                //得到对应position的数据集
                TypeItemData itemData = (TypeItemData) dataList.get(position).getT();
                ((TypeViewHolder)holder).mTextView.setText(itemData.tvStr);
            }
            //如果holder是ButtonViewHolder的实例
            if (holder instanceof TitleViewHolder) {
                //得到对应position的数据集
                TypeItemData itemData = (TypeItemData) dataList.get(position).getT();
                ((TitleViewHolder)holder).title.setText(itemData.tvStr);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return dataList.get(position).getDataType();
        }

        public void notifyItemMoved2(int fromPosition, int toPosition){
            Collections.swap(dataList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public class TypeItemData {
        String tvStr;
        public TypeItemData(String tv){
            this.tvStr = tv;
        }
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImgLeft;
        public ImageView mImgRight;

        public TypeViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_type);
            mImgLeft = (ImageView) itemView.findViewById(R.id.img_chosen);
            mImgRight = (ImageView) itemView.findViewById(R.id.img_tap);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}