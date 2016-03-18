package com.example.ios.scrolldelete;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.util.HashMap;

/**
 * Created by BG207369 on 2016/1/19.
 */
public class HorizontalScrollLinearLayout extends LinearLayout implements View.OnTouchListener {
    private Scroller mScroller;
    boolean mSwiping = true;
    boolean mItemPressed = false;
    float mDownX;
    private int mSwipeSlop = -1;
    private int mDistance;
    //scroll duration
    private int mDuration = 500;


    public HorizontalScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        mScroller = new Scroller(context);
    }

    public HorizontalScrollLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollLinearLayout(Context context) {
        this(context, null);
    }

    public void scrollDistance(int distance) {
        this.mDistance = distance;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }


    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }


    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {

        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, mDuration);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
                mItemPressed = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX() + v.getTranslationX();
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                //滑到最后或者scroll x为0，view不能被swipe
                if ((mScroller.getFinalX() == 0 && deltaX > 0) || (mScroller.getFinalX() == mDistance && deltaX < 0)) {
                    mSwiping = false;
                }
                if (deltaXAbs > mSwipeSlop && deltaXAbs < mDistance && mSwiping) {
                    if (deltaX < 0)
                        smoothScrollTo(-(int) deltaX, 0);
                    else {
                        smoothScrollTo(mDistance - (int) deltaX, 0);
                    }
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                // User let go - figure out whether to animate the view out, or back into place
                if (mSwiping) {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (deltaXAbs > 20) {
                        // Greater than a quarter of the width - animate it out
                        smoothScrollTo(deltaX < 0 ? mDistance : 0, 0);
                    }
                }
                mSwiping = true;
            }
            mItemPressed = false;
            break;
            default:
                return false;

        }

        return true;
    }


}
