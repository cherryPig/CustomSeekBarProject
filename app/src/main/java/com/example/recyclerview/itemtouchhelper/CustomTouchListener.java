package com.example.recyclerview.itemtouchhelper;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import com.example.android.listviewremovalanimation.BackgroundContainer;

import java.util.HashMap;

/**
 * Created by BG207369 on 2016/1/18.
 */
public class CustomTouchListener implements View.OnTouchListener {
    boolean mSwiping = false;
    boolean mItemPressed = false;
    float mDownX;
    private int mSwipeSlop = -1;
    private boolean isChild;
    //    private RecyclerView mRecyclerView;
    private ItemTouchHelperListener mListener;

    BackgroundContainer mBackgroundContainer;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    public interface ItemTouchHelperListener {
        void onItemSwipe(View view);

        boolean onItemTouched(View v, MotionEvent event);
    }

    public CustomTouchListener() {
        this(null);
    }

    public CustomTouchListener(ItemTouchHelperListener listener) {
        this.mListener = listener;
    }

//
//    public void attachView(RecyclerView recyclerView) {
//        this.mRecyclerView = recyclerView;
//    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        boolean flag = false;
        if (mListener != null) {
            flag = mListener.onItemTouched(v, event);
        }
        if (flag)
            return false;
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
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX() + v.getTranslationX();
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
//                        mRecyclerView.requestDisallowInterceptTouchEvent(true);
//                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                    }
                }
                if (mSwiping) {
                    v.setTranslationX((x - mDownX));
                    v.setAlpha(1 - deltaXAbs / v.getWidth());
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
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
//                    mRecyclerView.setEnabled(false);
                    v.animate().setDuration(duration).
                            alpha(endAlpha).translationX(endX).
                            withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Restore animated values
                                    v.setAlpha(1);
                                    v.setTranslationX(0);
                                    if (remove) {
//                                        RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder((View) v.getParent());
                                        if (mListener != null) {
                                            mListener.onItemSwipe(v);
                                        }

//                                            animateRemoval(mListView, v);
                                    } else {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
//                                        mRecyclerView.setEnabled(true);
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

}
