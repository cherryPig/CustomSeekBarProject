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

package com.example.android.listviewremovalanimation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import best.customseekbarproject.R;

public class StableArrayAdapter extends RecyclerView.Adapter<StableArrayAdapter.MyViewHolder> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    private List<String> mObject;
    private BackgroundContainer container;

    public StableArrayAdapter(List<String> objects,BackgroundContainer container) {
        this.mObject = objects;
        this.container = container;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opaque_text_view,null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        CustomTouchListener childListener = new CustomTouchListener(true,container,this);
        CustomTouchListener parentListener = new CustomTouchListener(false,container,this);
        viewHolder.mRollBackTextView.setOnTouchListener(childListener);
        viewHolder.mText.setOnTouchListener(childListener);
        view.setOnTouchListener(parentListener);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mObject.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(mObject.get(position));
 }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView mText,mRollBackTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mText =(TextView) itemView.findViewById(R.id.text);
            mRollBackTextView = (TextView)itemView.findViewById(R.id.roll_back);
        }
        public void setData(String str){
            ((TextView) itemView.findViewById(R.id.text)).setText(str);
        }
    }

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     */
    class  CustomTouchListener implements View.OnTouchListener {

        boolean mSwiping = false;
        boolean mItemPressed = false;
        float mDownX;
        private int mSwipeSlop = -1;
        private boolean isChild;
        private RecyclerView mListView;
        private RecyclerView.Adapter mAdapter;

        BackgroundContainer mBackgroundContainer;
        HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

        private static final int SWIPE_DURATION = 250;
        private static final int MOVE_DURATION = 150;



        public CustomTouchListener(boolean isChild,BackgroundContainer container,RecyclerView.Adapter adapter){
            this.isChild = isChild;
            this.mBackgroundContainer = container;
            this.mAdapter = adapter;
        }

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            mListView.requestDisallowInterceptTouchEvent(true);
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        mListView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove && !isChild) {
                                            int index = mListView.getChildAdapterPosition(v);
                                            mAdapter.notifyItemRemoved(index);
//                                            animateRemoval(mListView, v);
                                        } else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            mListView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
//    private void animateRemoval(final ListView listview, View viewToRemove) {
//        int firstVisiblePosition = listview.getFirstVisiblePosition();
//        for (int i = 0; i < listview.getChildCount(); ++i) {
//            View child = listview.getChildAt(i);
//            if (child != viewToRemove) {
//                int position = firstVisiblePosition + i;
//                long itemId = mAdapter.getItemId(position);
//                mItemIdTopMap.put(itemId, child.getTop());
//            }
//        }
//        // Delete the item from the adapter
//        int position = mListView.getPositionForView(viewToRemove);
//        mAdapter.remove(mAdapter.getItem(position));
//
//        final ViewTreeObserver observer = listview.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                observer.removeOnPreDrawListener(this);
//                boolean firstAnimation = true;
//                int firstVisiblePosition = listview.getFirstVisiblePosition();
//                for (int i = 0; i < listview.getChildCount(); ++i) {
//                    final View child = listview.getChildAt(i);
//                    int position = firstVisiblePosition + i;
//                    long itemId = mAdapter.getItemId(position);
//                    Integer startTop = mItemIdTopMap.get(itemId);
//                    int top = child.getTop();
//                    if (startTop != null) {
//                        if (startTop != top) {
//                            int delta = startTop - top;
//                            child.setTranslationY(delta);
//                            child.animate().setDuration(MOVE_DURATION).translationY(0);
//                            if (firstAnimation) {
//                                child.animate().withEndAction(new Runnable() {
//                                    public void run() {
//                                        mBackgroundContainer.hideBackground();
//                                        mSwiping = false;
//                                        mListView.setEnabled(true);
//                                    }
//                                });
//                                firstAnimation = false;
//                            }
//                        }
//                    } else {
//                        // Animate new views along with the others. The catch is that they did not
//                        // exist in the start state, so we must calculate their starting position
//                        // based on neighboring views.
//                        int childHeight = child.getHeight() + listview.getDividerHeight();
//                        startTop = top + (i > 0 ? childHeight : -childHeight);
//                        int delta = startTop - top;
//                        child.setTranslationY(delta);
//                        child.animate().setDuration(MOVE_DURATION).translationY(0);
//                        if (firstAnimation) {
//                            child.animate().withEndAction(new Runnable() {
//                                public void run() {
//                                    mBackgroundContainer.hideBackground();
//                                    mSwiping = false;
//                                    mListView.setEnabled(true);
//                                }
//                            });
//                            firstAnimation = false;
//                        }
//                    }
//                }
//                mItemIdTopMap.clear();
//                return true;
//            }
//        });
//    }


}
