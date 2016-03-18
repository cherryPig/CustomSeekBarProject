package com.example.recyclerview.itemtouchhelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by BG207369 on 2015/12/23.
 */
public class ItemHelperCallBack extends ItemTouchHelper.Callback {
    private ItemTouchListener itemTouchListener;

    public interface ItemTouchListener {
        /**
         * movement允许的方向
         *
         * @param index
         * @return
         */
        int getMovementDirection(int index);

        /**
         * swipe 允许的方向
         *
         * @param index
         * @return
         */
        int getSwipeDirection(int index);

        /**
         * swipe item
         *
         * @param index
         */
        void onItemSwipe(int index);

        /**
         * 移动item
         *
         * @param start  开始位置
         * @param target 终点位置
         * @return
         */
        boolean onItemMoved(int start, int target);

        /**
         * 是否swipe
         * @return
         */
        boolean isItemViewSwipeEnabled();
    }

    public ItemHelperCallBack(ItemTouchListener listener) {
        this.itemTouchListener = listener;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        Log.i("callback","isItemViewSwipeEnabled");

        if(itemTouchListener !=null)
            return itemTouchListener.isItemViewSwipeEnabled();
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.i("callback","getMovementFlags");

        if (itemTouchListener != null) {
            return makeMovementFlags(itemTouchListener.getMovementDirection(viewHolder.getAdapterPosition()), itemTouchListener.getSwipeDirection(viewHolder.getAdapterPosition()));
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.i("callback","onMove");
        if (itemTouchListener != null)
            return itemTouchListener.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("callback","onSwiped");

        if (itemTouchListener != null) {
            itemTouchListener.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        Log.i("callback","getSwipeThreshold");

        return super.getSwipeThreshold(viewHolder);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.i("callback","onSelectedChanged");
        super.onSelectedChanged(viewHolder, actionState);
    }


}
