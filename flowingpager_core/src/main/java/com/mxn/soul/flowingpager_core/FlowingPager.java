package com.mxn.soul.flowingpager_core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_OPEN;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_OPEN_SMOOTH;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_UP_AUTO;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.OPEN_RATIO;

/**
 * Created by mxn on 2019/7/1.
 * FlowingPager
 */
public class FlowingPager extends ElasticPager {

    public FlowingPager(Context context) {
        super(context);
    }

    public FlowingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowingPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @SuppressLint("NewApi")
    @Override
    protected void initPager(Context context, AttributeSet attrs, int defStyle) {
        super.initPager(context, attrs, defStyle);
    }



    @Override
    // 只用于menu的位移
    protected void onMenuOffsetChanged(int offsetPixels, int type) {
        if (getPosition() == Position.LEFT) {
            if (type == TYPE_UP_AUTO  || type == TYPE_OPEN_SMOOTH || type == TYPE_OPEN) {
                mContentContainer.setTranslationX((float) (offsetPixels + mCrackSize ));
                mMenuContainer.setTranslationX((float) (offsetPixels - mMenuSize + mCrackSize));
            } else {
                mContentContainer.setTranslationX((float) (offsetPixels - mCrackSize));
                mMenuContainer.setTranslationX((float) (offsetPixels - mMenuSize ));
            }
        } else {
            if (type == TYPE_UP_AUTO  || type == TYPE_OPEN_SMOOTH || type == TYPE_OPEN) {
                mContentContainer.setTranslationX((float) (offsetPixels - mCrackSize));
                mMenuContainer.setTranslationX((float) (offsetPixels + mMenuSize - mCrackSize));
            } else {
                mContentContainer.setTranslationX((float) (offsetPixels + mCrackSize));
                mMenuContainer.setTranslationX((float) (offsetPixels + mMenuSize ));
            }
        }

        invalidate();
    }


    @Override
    protected void onOffsetPixelsChanged(int offsetPixels) {
        switch (getPosition()) {
            case Position.LEFT:
                mFlowingButton.setTranslationX(offsetPixels);
                break;
            case Position.RIGHT:
                mFlowingButton.setTranslationX(offsetPixels);
                break;
        }
        invalidate();
    }

    @Override
    public void openMenu(boolean animate) {
        openMenu(animate, getHeight() / 2);
    }

    @Override
    public void openMenu(boolean animate, float y) {
    }

    @Override
    public void closeMenu(boolean animate) {
        closeMenu(animate, getHeight() / 2);
    }

    @Override
    public void closeMenu(boolean animate, float y) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switch (getPosition()) {
            case Position.LEFT:
                mMenuContainer.setTranslationX( - mMenuSize + mCrackSize);

                break;
            case Position.RIGHT:
                mMenuContainer.setTranslationX( mMenuSize - mCrackSize);
                break;
        }
        onOffsetPixelsChanged((int) mOffsetPixels);
    }

    @SuppressLint("NewApi")
    @Override
    protected void startLayerTranslation() {
        if (mHardwareLayersEnabled && !mLayerTypeHardware) {
            mLayerTypeHardware = true;
            mMenuContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void stopLayerTranslation() {
        if (mLayerTypeHardware) {
            mLayerTypeHardware = false;
            mMenuContainer.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("Must measure with an exact size");
        }

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mOffsetPixels == -1) {
            openMenu(false);
        }

        int menuWidthMeasureSpec;
        int menuHeightMeasureSpec;
        menuWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, mMenuSize);
        menuHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, height);
        mMenuContainer.measure(menuWidthMeasureSpec, menuHeightMeasureSpec);

        final int contentWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, height);
        mContentContainer.measure(contentWidthMeasureSpec, contentHeightMeasureSpec);

        int buttonWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, mButtonViewSize);
        int buttonHeightMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, height);
        mFlowingButton.measure(buttonWidthMeasureSpec, buttonHeightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;

        mContentContainer.layout(0, 0, width, height);

        switch (getPosition()) {
            case Position.LEFT:
                mMenuContainer.layout(0, 0, mMenuSize, height);
                if (mButtonIconMarginTop > 0  ) {
                    mButtonIconMarginBottom = height - mButtonIconMarginTop - mButtonViewSize ;
                }
                mFlowingButton.layout(0, b - mButtonIconMarginBottom - mButtonViewSize, mButtonViewSize, b - mButtonIconMarginBottom);
                break;
            case Position.RIGHT:
                mMenuContainer.layout(0, 0, mMenuSize, height);
                if (mButtonIconMarginTop > 0  ) {
                    mButtonIconMarginBottom = height - mButtonIconMarginTop - mButtonViewSize ;
                }
                mFlowingButton.layout(width - mButtonViewSize, b - mButtonIconMarginBottom - mButtonViewSize, width, b - mButtonIconMarginBottom);
                break;
        }
    }


    private boolean isButtonTouch(int x , int y ) {
        boolean buttonTouch = ViewHelper.getRight(mFlowingButton) > x && ViewHelper.getLeft(mFlowingButton) < x
                & ViewHelper.getTop(mFlowingButton) < y && ViewHelper.getBottom(mFlowingButton) > y ;
        return buttonTouch;
    }

    private boolean willCloseEnough() {
        boolean closeEnough = false;
        switch (getPosition()) {
            case Position.LEFT:
                closeEnough = mOffsetPixels <= mMenuSize * OPEN_RATIO;
                break;
            case Position.RIGHT:
                closeEnough = -mOffsetPixels <= mMenuSize * OPEN_RATIO;
                break;
        }
        return closeEnough;
    }


    private boolean willOpenEnough() {
        boolean openEnough = false;
        switch (getPosition()) {
            case Position.LEFT:
                openEnough = mMenuSize - mButtonViewSize - mOffsetPixels <= mMenuSize * OPEN_RATIO;
                break;
            case Position.RIGHT:
                openEnough = -mMenuSize + mButtonViewSize - mOffsetPixels >= -mMenuSize * OPEN_RATIO;
                break;
        }
        return openEnough;
    }

    protected boolean onDownAllowDrag() {
        return isButtonTouch((int)mInitialMotionX, (int)mInitialMotionY) && getPagerState() != STATE_CLOSING && getPagerState() != STATE_OPENING ;
    }

    protected boolean onMoveAllowDrag(int x, float dx) {
        switch (getPosition()) {
            case Position.LEFT:

                return ((!mMenuVisible && isButtonTouch((int)mInitialMotionX, (int)mInitialMotionY) && (dx > 0)) //  closed
                        || (mMenuVisible && isButtonTouch((int)mInitialMotionX, (int)mInitialMotionY) && (dx < 0)))
                        && getPagerState() != STATE_CLOSING && getPagerState() != STATE_OPENING;//  open
            case Position.RIGHT:
                return ((!mMenuVisible && isButtonTouch((int)mInitialMotionX, (int)mInitialMotionY) && (dx < 0)) //  closed
                        || (mMenuVisible && isButtonTouch((int)mInitialMotionX, (int)mInitialMotionY) && (dx > 0)))
                        && getPagerState() != STATE_CLOSING && getPagerState() != STATE_OPENING;//  open
        }

        return false;
    }

    protected void onMoveEvent(float dx, float y, int type) {
        switch (getPosition()) {
            case Position.LEFT:
                setOffsetPixels(Math.min(Math.max(mOffsetPixels + dx, 0), mMenuSize - mButtonViewSize), y, type);
                break;
            case Position.RIGHT:
                setOffsetPixels(Math.max(Math.min(mOffsetPixels + dx, 0), -mMenuSize +  mButtonViewSize), y, type);
                break;
        }
    }

    protected void onUpEvent(int x, int y) {
        switch (getPosition()) {
            case Position.LEFT: {
                if (mIsDragging) {
                    if (mPagerState == STATE_DRAGGING_CLOSE && willOpenEnough()) {
                        smoothOpen(y);
                        return;
                    }
                    if (mPagerState == STATE_DRAGGING_OPEN && willCloseEnough()) {
                        smoothClose(y);
                        return;
                    }
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mLastMotionX = x;
                } else if (isFirstPointUp) {
                    isFirstPointUp = false;
                    return;
                }
                // Close the menu when content is clicked while the menu is visible.
                else if (mMenuVisible) {
                    closeMenu(true, y);
                }
                break;
            }
            case Position.RIGHT: {
                if (mIsDragging) {
                    if (mPagerState == STATE_DRAGGING_CLOSE && willOpenEnough()) {
                        smoothOpen(y);
                        return;
                    }
                    if (mPagerState == STATE_DRAGGING_OPEN && willCloseEnough()) {
                        smoothClose(y);
                        return;
                    }
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    mLastMotionX = x;
                } else if (isFirstPointUp) {
                    isFirstPointUp = false;
                    return;
                }
                // Close the menu when content is clicked while the menu is visible.
                else if (mMenuVisible) {
                    closeMenu(true, y);
                }
                break;
            }
        }
    }

    protected boolean checkTouchSlop(float dx, float dy) {
        return Math.abs(dx) > mTouchSlop && Math.abs(dx) > Math.abs(dy);
    }

    @Override
    protected void stopAnimation() {
        super.stopAnimation();
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private void onPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mActivePointerId = INVALID_POINTER;
            mIsDragging = false;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return false;
        }
        // TODO mMenuVisible CHECK
        if (action == MotionEvent.ACTION_DOWN && mMenuVisible && isOpenEnough()) {
            setOffsetPixels(mMenuSize- mCrackSize, 0, TYPE_OPEN);
            stopAnimation();
            setPagerState(STATE_OPEN);
            mIsDragging = false;
        }
        if (action == MotionEvent.ACTION_DOWN && mMenuVisible && isCloseEnough()) {
            setOffsetPixels(0, 0, FlowingMenuLayout.TYPE_NONE);
            stopAnimation();
            setPagerState(STATE_CLOSED);
            mIsDragging = false;
        }

        // todo
        if (action != MotionEvent.ACTION_DOWN && mIsDragging) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                final boolean allowDrag = onDownAllowDrag();
                mActivePointerId = ev.getPointerId(0);

                if (allowDrag) {
                    setPagerState(mMenuVisible ? STATE_OPEN : STATE_CLOSED);
                    stopAnimation();
                    mIsDragging = false;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    mIsDragging = false;
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                    // todo
//                    closeMenu(true, ev.getY());
                    return false;
                }
                final float x = ev.getX(pointerIndex);
                final float dx = x - mLastMotionX;
                final float y = ev.getY(pointerIndex);
                final float dy = y - mLastMotionY;

                if (checkTouchSlop(dx, dy)) {

                    final boolean allowDrag = onMoveAllowDrag((int) x, dx);
                    if (allowDrag) {
                        stopAnimation();
                        if (mPagerState == STATE_OPEN || mPagerState == STATE_OPENING) {
                            setPagerState(STATE_DRAGGING_CLOSE);
                        } else {
                            setPagerState(STATE_DRAGGING_OPEN);
                        }
                        mIsDragging = true;
                        mLastMotionX = x;
                        mLastMotionY = y;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        return mIsDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                final boolean allowDrag = onDownAllowDrag();
                mActivePointerId = ev.getPointerId(0);
                if (allowDrag) {
                    stopAnimation();
                    startLayerTranslation();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex == -1) {
                    mIsDragging = false;
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                    //TODO  OR OPEN
                    closeMenu(true, ev.getY());
                    return false;
                }
                if (mIsDragging) {
                    startLayerTranslation();
                    final float x = ev.getX(pointerIndex);
                    final float dx = x - mLastMotionX;
                    final float y = ev.getY(pointerIndex);
                    mLastMotionX = x;
                    mLastMotionY = y;
                    if (mPagerState == STATE_DRAGGING_OPEN) {
                        if (getPosition() == ElasticPager.Position.LEFT) {
                            if (mOffsetPixels + dx < getWidth() * OPEN_RATIO) {
                                onMoveEvent(dx, y, FlowingMenuLayout.TYPE_UP_MANUAL);
                            } else {
                                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                                mLastMotionX = x;
                                animateButtonOffsetTo(mMenuSize - mButtonViewSize, y);
                                animateMenuOffsetTo(mMenuSize -   2 * mCrackSize, y);
                                isFirstPointUp = true;
                                endDrag();
                            }
                        } else {
                            if (mOffsetPixels + dx > - getWidth() * OPEN_RATIO) {
                                onMoveEvent(dx, y, FlowingMenuLayout.TYPE_UP_MANUAL);
                            } else {
                                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                                mLastMotionX = x;
                                animateButtonOffsetTo(-mMenuSize + mButtonViewSize, y);
                                animateMenuOffsetTo(-mMenuSize + 2 * mCrackSize, y);
                                isFirstPointUp = true;
                                endDrag();
                            }
                        }
                    } else if (mPagerState == STATE_DRAGGING_CLOSE) {
                        if (getPosition() == ElasticPager.Position.LEFT) {
                            // mMenuSize - mButtonViewSize为mOffsetPixels终点
                            if (mMenuSize - mButtonViewSize - mOffsetPixels - dx < getWidth() * OPEN_RATIO) {
                                onMoveEvent(dx, y, FlowingMenuLayout.TYPE_DOWN_MANUAL);
                            } else {
                                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                                mLastMotionX = x;
                                animateButtonOffsetTo( 0, y);
                                animateMenuOffsetTo(  0 , y);
                                isFirstPointUp = true;
                                endDrag();
                            }
                        } else {
                            if (-mMenuSize + mButtonViewSize - mOffsetPixels - dx > - getWidth() * OPEN_RATIO) {
                                onMoveEvent(dx, y, FlowingMenuLayout.TYPE_DOWN_MANUAL);
                            } else {
                                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                                mLastMotionX = x;
                                animateButtonOffsetTo( 0, y);
                                animateMenuOffsetTo(  0 , y);
                                isFirstPointUp = true;
                                endDrag();
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                int index = ev.findPointerIndex(mActivePointerId);
                index = index == -1 ? 0 : index;
                final int x = (int) ev.getX(index);
                final int y = (int) ev.getY(index);
                onUpEvent(x, y);
                mActivePointerId = INVALID_POINTER;
                mIsDragging = false;
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:
                final int index = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                mLastMotionX = ev.getX(index);
                mLastMotionY = ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }
        return true;
    }
}
