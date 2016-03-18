/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.recyclerview.itemtouchhelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.listviewremovalanimation.BackgroundContainer;

import java.util.HashMap;
import java.util.List;

import best.customseekbarproject.R;

public class StableArrayAdapter extends RecyclerView.Adapter<StableArrayAdapter.MyViewHolder> implements ItemHelperCallBack.ItemTouchListener {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    private List<String> mObject;
    int index;
    boolean flag;

    public StableArrayAdapter(List<String> objects) {
        this.mObject = objects;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opaque_text_view, null);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mObject.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.i("adapter", "into[onBindViewHolder]position:" + position);
        holder.setData(mObject.get(position), position);
        flag = holder.isWaitingForDelete();
        holder.mRollBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);
                flag = holder.isWaitingForDelete();

            }
        });
        holder.mText.setOnTouchListener(new CustomTouchListener(new CustomTouchListener.ItemTouchHelperListener() {

            @Override
            public void onItemSwipe(View view) {
                holder.displayTips();
                flag = holder.isWaitingForDelete();
            }

            @Override
            public boolean onItemTouched(View v, MotionEvent event) {
                index = position;
                if (position == 1 || position == 0)
                    return true;
                return false;
            }
        }));
    }

    @Override
    public int getMovementDirection(int index) {
        return 0;
    }

    @Override
    public int getSwipeDirection(int index) {
        int dragFlag = ItemTouchHelper.LEFT;
        return dragFlag;
    }

    @Override
    public void onItemSwipe(int index) {
        notifyItemRemoved(index);
        flag = false;
    }

    @Override
    public boolean onItemMoved(int start, int target) {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if (!flag)
            return false;
        if (index == 0 || index == 1)
            return false;
        return true;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mText, mRollBackTextView;

        public View mItemView;

        private int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mText = (TextView) itemView.findViewById(R.id.text);
            mRollBackTextView = (TextView) itemView.findViewById(R.id.roll_back);
        }


        public void setData(String str, int position) {

            mText.setVisibility(View.VISIBLE);
            mRollBackTextView.setVisibility(View.GONE);
            mRollBackTextView.setText("");
            mText.setText(str);

        }

        public boolean isWaitingForDelete() {
            return mRollBackTextView.getVisibility() == View.VISIBLE ? true : false;
        }

        public void displayTips() {
            mRollBackTextView.setVisibility(View.VISIBLE);
            mRollBackTextView.setText("取消");
            mText.setVisibility(View.GONE);
        }
    }


}
