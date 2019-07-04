
package com.mxn.soul.flowingpager_core;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;



/**
 * Created by mxn on 2016/10/15.
 * ElasticPager
 */
public abstract class ElasticPager extends ViewGroup {

    /**
     * Tag used when logging.
     */
    private static final String TAG = "ElasticPager";

    /**
     * Indicates whether debug code should be enabled.
     */
    private static final boolean DEBUG = false;
    /**
     * The time between each frame when animating the pager.
     */
    protected static final int ANIMATION_DELAY = 1000 / 60;

    /**
     * Indicates whether the menu is currently visible.
     */
    protected boolean mMenuVisible;
    /**
     * The size of the menu (width or height depending on the gravity).
     */
    protected int mMenuSize;

    /**
     * The size of the floating button view (width or height depending on the gravity).
     */
    protected int mButtonViewSize;

    protected int mCrackSize;

    /**
     * Interpolator used when animating the pager open/closed.
     */
    protected static final Interpolator SMOOTH_INTERPOLATOR = new DecelerateInterpolator(0.8f);

    /**
     * Slop before starting a drag.
     */
    protected int mTouchSlop;
    /**
     * Maximum velocity allowed when animating the pager open/closed.
     */
    protected int mMaxVelocity;
    /**
     * Scroller used when animating the pager open/closed.
     */
    private Scroller mScroller;
    private Scroller mScrollerMenu;
    /**
     * Indicates whether the current layer type is {@link android.view.View#LAYER_TYPE_HARDWARE}.
     */
    protected boolean mLayerTypeHardware;
    /**
     * Indicates whether to use {@link View#LAYER_TYPE_HARDWARE} when animating the pager.
     */
    protected boolean mHardwareLayersEnabled = false;

    /**
     * The initial X position of a drag.
     */
    protected float mInitialMotionX;

    /**
     * The initial Y position of a drag.
     */
    protected float mInitialMotionY;

    /**
     * The last X position of a drag.
     */
    protected float mLastMotionX = -1;

    /**
     * The last Y position of a drag.
     */
    protected float mLastMotionY = -1;

    /**
     * Velocity tracker used when animating the pager open/closed after a drag.
     */
    protected VelocityTracker mVelocityTracker;

    /**
     * Distance in px from closed position from where the pager is considered closed with regards to touch events.
     */
    protected int mCloseEnough;

    /**
     * The position of the pager.
     */
    private int mPosition;

    /**
     * The parent of the menu view.
     */
    protected BuildLayerFrameLayout mMenuContainer;

    /**
     * The parent of the floating button view.
     */
    protected View mFlowingButton;

    /**
     * The parent of the content view.
     */
    protected BuildLayerFrameLayout mContentContainer;
    /**
     * The custom menu view set by the user.
     */
    private FlowingMenuLayout mMenuView;
    /**
     * The custom content view set by the user.
     */
    private FlowingContentLayout mContentView;

    /**
     * The color of the menu.
     */
    protected int mMenuBackground;
    /**
     * The color of the content.
     */
    protected int mContentBackground;
    /**
     * Current offset.
     */
    protected float mOffsetPixels;
    protected float mMenuOffsetPixels;

    /**
     * The default button view size of the pager in dp.
     */
    private static final int DEFAULT_CRACK_SIZE = 15;
    private static final int DEFAULT_BUTTON_SIZE = 450;
    /**
     * Distance in dp from closed position from where the pager is considered closed with regards to touch events.
     */
    private static final int CLOSE_ENOUGH = 3;

    /**
     * Listener used to dispatch state change events.
     */
    private OnPagerStateChangeListener mOnPagerStateChangeListener;

    /**
     * The maximum animation duration.
     */
    private static final int DEFAULT_ANIMATION_DURATION = 300;
    /**
     * The maximum duration of open/close animations.
     */
    protected int mMaxAnimationDuration = DEFAULT_ANIMATION_DURATION;
    /**
     * Indicates that the pager is currently closed.
     */
    public static final int STATE_CLOSED = 0;

    /**
     * Indicates that the pager is currently closing.
     */
    public static final int STATE_CLOSING = 1;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int STATE_DRAGGING_OPEN = 2;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int STATE_DRAGGING_CLOSE = 4;

    /**
     * Indicates that the pager is currently opening.
     */
    public static final int STATE_OPENING = 6;

    /**
     * Indicates that the pager is currently open.
     */
    public static final int STATE_OPEN = 8;

    /**
     * The current pager state.
     *
     * @see #STATE_CLOSED
     * @see #STATE_CLOSING
     * @see #STATE_DRAGGING_OPEN
     * @see #STATE_DRAGGING_CLOSE
     * @see #STATE_OPENING
     * @see #STATE_OPEN
     */
    protected int mPagerState = STATE_CLOSED;

    /**
     * Bundle used to hold the pager state.
     */
    protected Bundle mState;

    /**
     * Key used when saving menu visibility state.
     */
    private static final String STATE_MENU_VISIBLE = "ElasticPager.menuVisible";
    /**
     * Indicates whether the pager is currently being dragged.
     */
    protected boolean mIsDragging;
    /**
     * The current pointer id.
     */
    protected int mActivePointerId = INVALID_POINTER;

    public static final int INVALID_POINTER = -1;

    private float eventY;

    protected boolean isFirstPointUp;


    public int mButtonIconMarginBottom ;
    public int mButtonIconMarginTop ;
    public float mSlideRange ;
    /**
     * Runnable used when animating the pager open/closed.
     */
    private final Runnable mDragRunnable = new Runnable() {
        @Override
        public void run() {
            postAnimationInvalidate();
        }
    };

    private final Runnable mMenuRunnable = new Runnable() {
        @Override
        public void run() {
            postMenuAnimationInvalidate();
        }
    };

    public ElasticPager(Context context) {
        super(context);
    }

    public ElasticPager(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.elasticPagerStyle);
    }

    public ElasticPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPager(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    protected void initPager(Context context, AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);
        setFocusable(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElasticPager);

        mMenuSize = FlowingPagerUtils.getScreenWidth(getContext());
        mCrackSize = a.getDimensionPixelSize(R.styleable.ElasticPager_edCrackWidth, dpToPx(DEFAULT_CRACK_SIZE));
        mButtonViewSize = a.getDimensionPixelSize(R.styleable.ElasticPager_edIconSize, dpToPx(DEFAULT_BUTTON_SIZE));
        mMenuBackground = a.getColor(R.styleable.ElasticPager_edMenuBackground, 0xFFdddddd);
        mContentBackground = a.getColor(R.styleable.ElasticPager_edContentBackground, 0xFFdddddd);
        mMaxAnimationDuration = a.getInt(R.styleable.ElasticPager_edMaxAnimationDuration, DEFAULT_ANIMATION_DURATION);
        final int position = a.getInt(R.styleable.ElasticPager_edPosition, 0);
        mButtonIconMarginBottom = a.getDimensionPixelSize(R.styleable.ElasticPager_edMarginBottom, dpToPx(DEFAULT_BUTTON_SIZE));
        mButtonIconMarginTop = a.getDimensionPixelSize(R.styleable.ElasticPager_edMarginTop, 0);
        mSlideRange = a.getFloat(R.styleable.ElasticPager_edSlideRange, 0);
        a.recycle();
        setPosition(position);

        FlowingPagerUtils.setOpenRatio(mSlideRange);
        mMenuContainer = new NoClickThroughFrameLayout(context);
        mMenuContainer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mContentContainer = new NoClickThroughFrameLayout(context);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context, SMOOTH_INTERPOLATOR);
        mScrollerMenu = new Scroller(context, SMOOTH_INTERPOLATOR);
        mCloseEnough = dpToPx(CLOSE_ENOUGH);

        mContentContainer.setLayerType(View.LAYER_TYPE_NONE, null);
        mContentContainer.setHardwareLayersEnabled(false);
    }

    protected int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * Callback interface for changing state of the pager.
     */
    public interface OnPagerStateChangeListener {

        /**
         * Called when the pager state changes.
         *
         * @param oldState The old pager state.
         * @param newState The new pager state.
         */
        void onPagerStateChange(int oldState, int newState);

        /**
         * Called when the pager slides.
         *
         * @param openRatio    Ratio for how open the menu is.
         * @param offsetPixels Current offset of the menu in pixels.
         */
        void onPagerSlide(float openRatio, int offsetPixels);
    }


    class Position {
        // Positions the pager to the left of the content.
        static final int LEFT = 1;
        // Positions the pager to the right of the content.
        static final int RIGHT = 2;
        /**
         * Position the pager at the start edge. This will position the pager to the {@link #LEFT} with LTR
         * languages and
         * {@link #RIGHT} with RTL languages.
         */
        static final int START = 3;
        /**
         * Position the pager at the end edge. This will position the pager to the {@link #RIGHT} with LTR
         * languages and
         * {@link #LEFT} with RTL languages.
         */
        static final int END = 4;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 3) {
            throw new IllegalStateException(
                    "child count isn't equal to 3 , content, button and Menu view must be added in xml .");
        }
        setBackgroundColor(mContentBackground);
        View content = getChildAt(0);
        if (content != null) {
            removeView(content);
            mContentView = (FlowingContentLayout) content;
            mContentView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mContentView.setPaintColor(mContentBackground);
            mContentView.setMenuPosition(getPosition());
            mContentView.setButtonIconSize(mButtonViewSize) ;
            mContentView.setButtonIconMarginBottom(mButtonIconMarginBottom) ;
            mContentView.setButtonIconMarginTop(mButtonIconMarginTop) ;
            mContentView.setCrackSize(mCrackSize) ;

            mContentContainer.removeAllViews();
            mContentContainer
                    .addView(content, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        } else {
            throw new IllegalStateException(
                    "content view must be added in xml .");
        }
        View menu = getChildAt(0);
        if (menu != null) {
            removeView(menu);
            mMenuView = (FlowingMenuLayout) menu;
            mMenuView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mMenuView.setPaintColor(mMenuBackground);
            mMenuView.setMenuPosition(getPosition());
            mMenuView.setButtonIconSize(mButtonViewSize) ;
            mMenuView.setButtonIconMarginBottom(mButtonIconMarginBottom) ;
            mMenuView.setButtonIconMarginTop(mButtonIconMarginTop) ;
            mMenuView.setCrackSize(mCrackSize) ;

            mMenuContainer.removeAllViews();
            mMenuContainer.addView(menu, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            throw new IllegalStateException(
                    "menu view must be added in xml .");
        }

        View button = getChildAt(0);
        if (button != null) {
            removeView(button);
            mFlowingButton = button;
        } else {
            throw new IllegalStateException(
                    "button view must be added in xml .");
        }

        addView(mContentContainer, -1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mMenuContainer, -1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mFlowingButton, -1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * Called when the number of pixels the content should be offset by has changed.
     *
     * @param offsetPixels The number of pixels to offset the content by.
     */
    protected abstract void onOffsetPixelsChanged(int offsetPixels);

    protected abstract void onMenuOffsetChanged(int offsetPixels , int type);
    /**
     * Toggles the menu open and close with animation.
     */
    public void toggleMenu() {
        toggleMenu(true);
    }

    /**
     * Toggles the menu open and close.
     *
     * @param animate Whether open/close should be animated.
     */
    public void toggleMenu(boolean animate) {
        if (mPagerState == STATE_OPEN || mPagerState == STATE_OPENING) {
            closeMenu(animate);
        } else if (mPagerState == STATE_CLOSED || mPagerState == STATE_CLOSING) {
            openMenu(animate);
        }
    }

    /**
     * Animates the menu open.
     */
    @SuppressWarnings("unused")
    public void openMenu() {
        openMenu(true);
    }

    /**
     * Opens the menu.
     *
     * @param animate Whether open/close should be animated.
     */
    public abstract void openMenu(boolean animate);

    public abstract void openMenu(boolean animate, float y);

    /**
     * Animates the menu closed.
     */
    @SuppressWarnings("unused")
    public void closeMenu() {
        closeMenu(true);
    }

    /**
     * Closes the menu.
     *
     * @param animate Whether open/close should be animated.
     */
    public abstract void closeMenu(boolean animate);

    public abstract void closeMenu(boolean animate, float y);

    /**
     * Indicates whether the menu is currently visible.
     *
     * @return True if the menu is open, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean isMenuVisible() {
        return mMenuVisible;
    }


    protected void smoothClose(final int eventY) {
        endDrag();
        setPagerState(STATE_CLOSING);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mOffsetPixels, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setOffsetPixels((Float) animation.getAnimatedValue(), eventY,
                        FlowingMenuLayout.TYPE_DOWN_SMOOTH);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                mMenuVisible = false;
                setOffsetPixels(0, 0, FlowingMenuLayout.TYPE_NONE);
                setPagerState(STATE_CLOSED);
                stopLayerTranslation();
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator(4f));
        valueAnimator.start();
    }

    protected void smoothOpen(final int eventY) {
        endDrag();
        setPagerState(STATE_OPENING);
        final float end ;
        if (getPosition() == Position.LEFT) {
            end = mMenuSize - mButtonViewSize ;
        } else {
            end = - mMenuSize + mButtonViewSize ;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mOffsetPixels, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setOffsetPixels((Float) animation.getAnimatedValue(), eventY,
                        FlowingMenuLayout.TYPE_OPEN_SMOOTH);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                mMenuVisible = true;
                setOffsetPixels(end, 0, FlowingMenuLayout.TYPE_OPEN);
                setPagerState(STATE_OPEN);
                stopLayerTranslation();
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator(4f));
        valueAnimator.start();
    }


    protected void animateMenuOffsetTo(int position, float eventY) {
        endDrag();
        int startX = (int) mMenuView.getMenuOffsetPixels();
        final int dx = position - startX;
        if (getPosition() == Position.LEFT) {
            if (dx < 0 ) {
                // menu和content移动距离不一样，需要按照移动距离长的来算（只处理关闭的时候）
                startX = startX +  mCrackSize ;
            }
        } else {
            if (dx > 0 ) {
                // menu和content移动距离不一样，需要按照移动距离长的来算（只处理关闭的时候）
                startX = startX - mCrackSize ;
            }
        }
        mScrollerMenu.startScroll(startX, 0, dx, 0, mMaxAnimationDuration);
        this.eventY = eventY;
        startLayerTranslation();
        postMenuAnimationInvalidate() ;
    }


    protected void animateButtonOffsetTo(int position, float eventY) {
        final int startX = (int) mOffsetPixels;
        final int dx = position - startX;
        if (dx == 0 ) {
            setOffsetPixels(position, 0, FlowingMenuLayout.TYPE_NONE);
            setPagerState(position == 0 ? STATE_CLOSED : STATE_OPEN);
            stopLayerTranslation();
            return;
        }
        animateOffsetTo(position, mMaxAnimationDuration, eventY);
    }


    protected void animateOffsetTo(int position, int duration, float eventY) {
        final int startX = (int) mOffsetPixels;
        final int dx = position - startX;
        if (getPosition() == Position.LEFT) {
            if (dx > 0) {
                setPagerState(STATE_OPENING);
            } else {
                setPagerState(STATE_CLOSING);
            }
        } else {
            if (dx > 0) {
                setPagerState(STATE_CLOSING);
            } else {
                setPagerState(STATE_OPENING);
            }
        }
        mScroller.startScroll(startX, 0, dx, 0, duration);
        this.eventY = eventY;
        startLayerTranslation();
        postAnimationInvalidate();
    }

    protected void setMenuOffsetPixels(float offsetPixels, float eventY, int type) {
        final int oldOffset = (int) mMenuOffsetPixels;
        final int newOffset = (int) offsetPixels;
        mMenuOffsetPixels = offsetPixels;
        mMenuView.setMenuOffsetPixels(mMenuOffsetPixels, eventY, type);
        // 需要多移動一個重合部分
        if (getPosition() == Position.LEFT) {
            mContentView.setContentOffsetPixels(mMenuOffsetPixels + mCrackSize , eventY, type);
        } else {
            mContentView.setContentOffsetPixels(mMenuOffsetPixels - mCrackSize , eventY, type);
        }

        if (newOffset != oldOffset) {
           onMenuOffsetChanged(newOffset ,type);
            // Notify any attached listeners of the current open ratio
            final float openRatio = ((float) Math.abs(newOffset)) / mMenuSize;
            dispatchOnPagerSlide(openRatio, newOffset);
        }
    }
    /**
     * Sets the number of pixels the content should be offset.
     *
     * @param offsetPixels The number of pixels to offset the content by.
     */
    protected void setOffsetPixels(float offsetPixels, float eventY, int type) {
        final int oldOffset = (int) mOffsetPixels;
        final int newOffset = (int) offsetPixels;
        mOffsetPixels = offsetPixels;
        mMenuView.setClipOffsetPixels(mOffsetPixels, eventY, type);
        mContentView.setClipOffsetPixels(mOffsetPixels, eventY, type);
        if (newOffset != oldOffset) {
            onOffsetPixelsChanged(newOffset);
            mMenuVisible = newOffset != 0;
            // Notify any attached listeners of the current open ratio
            final float openRatio = ((float) Math.abs(newOffset)) / mMenuSize;
            dispatchOnPagerSlide(openRatio, newOffset);
        }
    }


    private void setPosition(int position) {
        mPosition = position;
    }

    protected int getPosition() {
        final int layoutDirection = ViewHelper.getLayoutDirection(this);
        switch (mPosition) {
            case Position.START:
                if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                    return Position.RIGHT;
                } else {
                    return Position.LEFT;
                }
            case Position.END:
                if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                    return Position.LEFT;
                } else {
                    return Position.RIGHT;
                }
        }
        return mPosition;
    }

    /**
     * Register a callback to be invoked when the pager state changes.
     *
     * @param listener The callback that will run.
     */

    public void setOnPagerStateChangeListener(OnPagerStateChangeListener listener) {
        mOnPagerStateChangeListener = listener;
    }


    /**
     * Sets the maximum duration of open/close animations.
     *
     * @param duration The maximum duration in milliseconds.
     */
    @SuppressWarnings("unused")
    public void setMaxAnimationDuration(int duration) {
        mMaxAnimationDuration = duration;
    }

    @SuppressWarnings("unused")
    public ViewGroup getMenuContainer() {
        return mMenuContainer;
    }

    /**
     * Returns the ViewGroup used as a parent for the content view.
     *
     * @return The content view's parent.
     */
    @SuppressWarnings("unused")
    public ViewGroup getContentContainer() {
        return mContentContainer;
    }

    /**
     * Get the current state of the pager.
     *
     * @return The state of the pager.
     */
    @SuppressWarnings("unused")
    public int getPagerState() {
        return mPagerState;
    }

    protected void setPagerState(int state) {
        if (state != mPagerState) {
            final int oldState = mPagerState;
            mPagerState = state;
            if (mOnPagerStateChangeListener != null) {
                mOnPagerStateChangeListener.onPagerStateChange(oldState, state);
            }
            if (DEBUG) {
                logPagerState(state);
            }
        }
    }

    protected void logPagerState(int state) {
        switch (state) {
            case STATE_CLOSED:
                Log.d(TAG, "[PagerState] STATE_CLOSED");
                break;

            case STATE_CLOSING:
                Log.d(TAG, "[PagerState] STATE_CLOSING");
                break;

            case STATE_DRAGGING_CLOSE:
                Log.d(TAG, "[PagerState] STATE_DRAGGING_CLOSE");
                break;
            case STATE_DRAGGING_OPEN:
                Log.d(TAG, "[PagerState] STATE_DRAGGING_OPEN");
                break;

            case STATE_OPENING:
                Log.d(TAG, "[PagerState] STATE_OPENING");
                break;

            case STATE_OPEN:
                Log.d(TAG, "[PagerState] STATE_OPEN");
                break;
            default:
                Log.d(TAG, "[PagerState] Unknown: " + state);
        }
    }


    @Override
    public void postOnAnimation(Runnable action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.postOnAnimation(action);
        } else {
            postDelayed(action, ANIMATION_DELAY);
        }
    }

    protected void dispatchOnPagerSlide(float openRatio, int offsetPixels) {
        if (mOnPagerStateChangeListener != null) {
            mOnPagerStateChangeListener.onPagerSlide(openRatio, offsetPixels);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    void saveState(Bundle state) {
        final boolean menuVisible = mPagerState == STATE_OPEN || mPagerState == STATE_OPENING;
        state.putBoolean(STATE_MENU_VISIBLE, menuVisible);
    }

    /**
     * Restores the state of the pager.
     *
     * @param in A parcelable containing the pager state.
     */
    public void restoreState(Parcelable in) {
        mState = (Bundle) in;
        final boolean menuOpen = mState.getBoolean(STATE_MENU_VISIBLE);
        if (menuOpen) {
            openMenu(false);
        } else {
            setOffsetPixels(0, 0, FlowingMenuLayout.TYPE_NONE);
        }
        mPagerState = menuOpen ? STATE_OPEN : STATE_CLOSED;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);

        if (mState == null) {
            mState = new Bundle();
        }
        saveState(mState);

        state.mState = mState;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        restoreState(savedState.mState);
    }

    static class SavedState extends BaseSavedState {

        Bundle mState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressLint("ParcelClassLoader")
        SavedState(Parcel in) {
            super(in);
            mState = in.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(mState);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    protected float getXVelocity(VelocityTracker velocityTracker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return velocityTracker.getXVelocity(mActivePointerId);
        }

        return velocityTracker.getXVelocity();
    }


    protected boolean isCloseEnough() {
        return Math.abs(mOffsetPixels) <= mCloseEnough;
    }

    protected boolean isOpenEnough() {
        return Math.abs(mMenuSize- mCrackSize - mOffsetPixels) <= mCloseEnough;
    }

    /**
     * Callback when each frame in the pager animation should be drawn.
     */
    private void postAnimationInvalidate() {
        if (mScroller.computeScrollOffset()) {
            final int oldX = (int) mOffsetPixels;
            final int x = mScroller.getCurrX();
            if (x != oldX) {
                if (mPagerState == STATE_OPENING) {
                    setOffsetPixels(x, eventY, FlowingMenuLayout.TYPE_UP_AUTO);
                } else if (mPagerState == STATE_CLOSING) {
                    setOffsetPixels(x, eventY, FlowingMenuLayout.TYPE_DOWN_AUTO);
                }
            }
            if (x != mScroller.getFinalX()) {
                postOnAnimation(mDragRunnable);
                return;
            }
        }

        if (mPagerState == STATE_OPENING ) {

            mScroller.abortAnimation();
            final int finalX = mScroller.getFinalX();

            mMenuVisible = finalX != 0;

            setOffsetPixels(finalX, 0, FlowingMenuLayout.TYPE_OPEN);

            mScrollerMenu.abortAnimation();
            final int menuFinalX = mScrollerMenu.getFinalX();
            setMenuOffsetPixels(menuFinalX, 0, FlowingMenuLayout.TYPE_OPEN) ;
            setPagerState(finalX == 0 ? STATE_CLOSED : STATE_OPEN);
            stopLayerTranslation();

        } else if (mPagerState == STATE_CLOSING) {
            mScroller.abortAnimation();
            final int finalX = mScroller.getFinalX();
            mMenuVisible = finalX != 0;
            setOffsetPixels(finalX, 0, FlowingMenuLayout.TYPE_NONE);

            mScrollerMenu.abortAnimation();
            final int menuFinalX = mScrollerMenu.getFinalX();
            setMenuOffsetPixels(menuFinalX, 0, FlowingMenuLayout.TYPE_NONE) ;

            setPagerState(finalX == 0 ? STATE_CLOSED : STATE_OPEN);
            stopLayerTranslation();
        }

    }


    /**
     * Callback when each frame in the pager animation should be drawn.
     */
    private void postMenuAnimationInvalidate() {
        if (mScrollerMenu.computeScrollOffset()) {
            final int oldX = 0;
            final int x = mScrollerMenu.getCurrX();

            if (x != oldX) {
                if (mPagerState == STATE_OPENING) {
                    setMenuOffsetPixels(x, eventY, FlowingMenuLayout.TYPE_UP_AUTO);
                } else if (mPagerState == STATE_CLOSING) {
                    setMenuOffsetPixels(x, eventY, FlowingMenuLayout.TYPE_DOWN_AUTO);
                }
            }
            if (x != mScrollerMenu.getFinalX()) {
                postOnAnimation(mMenuRunnable);
            }
        }
    }

    /**
     * Called when a drag has been ended.
     */
    protected void endDrag() {
        mIsDragging = false;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Stops ongoing animation of the pager.
     */
    protected void stopAnimation() {
        removeCallbacks(mDragRunnable);
        mScroller.abortAnimation();
        stopLayerTranslation();
    }

    /**
     * If possible, set the layer type to {@link android.view.View#LAYER_TYPE_HARDWARE}.
     */
    @SuppressLint("NewApi")
    protected void startLayerTranslation() {
        if (mHardwareLayersEnabled && !mLayerTypeHardware) {
            mLayerTypeHardware = true;
            mContentContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mMenuContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    /**
     * If the current layer type is {@link android.view.View#LAYER_TYPE_HARDWARE}, this will set it to
     * {@link View#LAYER_TYPE_NONE}.
     */
    @SuppressLint("NewApi")
    protected void stopLayerTranslation() {
        if (mLayerTypeHardware) {
            mLayerTypeHardware = false;
            mContentContainer.setLayerType(View.LAYER_TYPE_NONE, null);
            mMenuContainer.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    @SuppressWarnings("unused")
    public void setHardwareLayerEnabled(boolean enabled) {
        if (enabled != mHardwareLayersEnabled) {
            mHardwareLayersEnabled = enabled;
            mMenuContainer.setHardwareLayersEnabled(enabled);
            mContentContainer.setHardwareLayersEnabled(enabled);
            stopLayerTranslation();
        }
    }

}
