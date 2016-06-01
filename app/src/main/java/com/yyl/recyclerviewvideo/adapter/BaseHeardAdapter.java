package com.yyl.recyclerviewvideo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyl on 2015/11/2/002.
 */
@SuppressWarnings("ALL")
public abstract class BaseHeardAdapter<T> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<T> mData = new ArrayList<>();

    private final int TYPE_HEADER = -1;
    private final int TYPE_FOOTER = -2;// 后期有用到在追加
    private final int TYPE_ITEM1 = 0;// 后期有用到在追加
    private boolean addHead = false;
    private boolean addFoot = false;

    public BaseHeardAdapter() {
        addHead = false;
        addFoot = false;
    }


    public void addAllItemNotify(ArrayList<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllItemNotify(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void addItemList(List<T> data) {
        mData.addAll(mData.size(), data);
    }

    public void clearAllItem() {
        mData.clear();
    }

    private RecyclerView.ViewHolder headerView;
    private RecyclerView.ViewHolder footView;

    public void removeHeaderView() {
        addHead = false;
        notifyDataSetChanged();
    }

    public void addHeaderView(View view) {
        this.headerView = new RecyclerView.ViewHolder(view) {
        };
        Log.i("yyl","setIsRecyclable1");
        headerView.setIsRecyclable(true);
        addHead = true;
        notifyDataSetChanged();
    }

    public void addFootView(View view) {
        this.footView = new RecyclerView.ViewHolder(view) {
        };
        addFoot = true;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                headerView.itemView.setAnimation(null);
                return headerView;
            case TYPE_FOOTER:
                //    footView.itemView.setAnimation(null);
                return footView;
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        getLayoutId(viewType), parent, false);
                return getHolder(view, viewType);
        }
    }

    public abstract int getLayoutId(int viewType);

    abstract BaseViewHolder getHolder(View view, int viewType);

    public class BaseViewHolder extends RecyclerView.ViewHolder {


        public BaseViewHolder(View itemView) {
            super(itemView);

        }

        void setData(T t, int position) {

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (addHead && position == 0) {
            //头view的分支
        } else if (isShowFootType(position)) {

        } else {
            if (addHead) {// 去掉头view的位置
                position -= 1;
            }
            ((BaseViewHolder) holder).setData(mData.get(position), position);
            if (addFoot) {
                position -= 1;
            }
            if (position == mData.size() - 1) {
                onLoadMoreItem();
            }
        }
    }

    protected abstract void onLoadMoreItem();

    @Override
    public int getItemCount() {
        if (isShowHead() && addFoot) {
            return mData.size() + 2;
        } else if (isShowHead() || isShowFoot()) {
            return mData.size() + 1;
        }
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowHead() && position == 0) {
            return TYPE_HEADER;
        } else if (isShowFootType(position)) {
            return TYPE_FOOTER;
        } else {
            return getOtherItemViewType(position);
        }
    }

    private boolean isShowFootType(int position) {
        if (addFoot) {
            int leng = position;
            if (addHead) {
                leng -= 1;
            }
            return mData.size() == leng;
        }
        return false;
    }

    /**
     * 子类view的类型扩展重写用这个方法
     */

    protected int getOtherItemViewType(int position) {
        return TYPE_ITEM1;
    }

    public boolean isShowHead() {
        return addHead;
    }

    public boolean isShowFoot() {
        return addFoot;
    }

    public void showHeaderView() {
        addHead = true;
    }

    public void hideHeaderView() {
        addHead = false;
    }


    public void showFootView() {
        addFoot = true;
    }

    public void hideFootView() {
        addFoot = false;
    }


    public class MyAdapterItemViewClick implements View.OnClickListener {
        int position;
        Object type;

        public MyAdapterItemViewClick(Object type, int position) {
            this.position = position;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            if (adapterItemClick != null) {
                adapterItemClick.myItemClick(position);
            }
            myAdapterItemClick(type, position);
        }
    }

    public void myAdapterItemClick(Object type, int position) {

    }

    public void setAdapterItemClick(MyAdapterItemClick adapterItemClick) {
        this.adapterItemClick = adapterItemClick;
    }

    public MyAdapterItemClick adapterItemClick;

    public interface MyAdapterItemClick {
        void myItemClick(int position);
    }

}
