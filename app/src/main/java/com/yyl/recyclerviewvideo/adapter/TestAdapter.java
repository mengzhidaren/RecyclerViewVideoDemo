package com.yyl.recyclerviewvideo.adapter;

import android.view.View;
import android.widget.TextView;


import com.yyl.recyclerviewvideo.R;
import com.yyl.recyclerviewvideo.mode.TestDao;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yyl on 2016/5/27/027.
 */
public class TestAdapter extends BaseHeardAdapter<TestDao> {

    public void init(){
        ArrayList<TestDao> list = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            TestDao testDao = new TestDao();
            testDao.name = "test=" + i;
            list.add(testDao);
        }
        addAllItemNotify(list);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_list1;
    }

    @Override
    BaseViewHolder getHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    protected void onLoadMoreItem() {

    }


    class ViewHolder extends BaseViewHolder {
        @Bind(R.id.test_text1)
        TextView testText1;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void setData(TestDao testDao, int position) {
            testText1.setText(testDao.name);
        }
    }
}
