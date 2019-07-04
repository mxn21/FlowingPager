package com.mxn.soul.flowingpager_core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF1;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF2;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF3;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF4;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF5;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF6;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF7;
import static com.mxn.soul.flowingpager_core.FlowingPagerUtils.COEF8;


/**
 * Created by mxn on 2019/7/1.
 * FlowingMenuLayout
 */

@SuppressWarnings("FieldCanBeLocal")
public class FlowingMenuLayout extends FrameLayout {

    private Path mClipPath;
    private float mClipOffsetPixels = 0;
    private float mMenuOffsetPixels = 0;

    public final static int TYPE_NONE = 0;
    public final static int TYPE_UP_MANUAL = 1;
    public final static int TYPE_UP_AUTO = 2;
    public final static int TYPE_DOWN_MANUAL = 4;
    public final static int TYPE_DOWN_AUTO = 5;
    public final static int TYPE_OPEN = 6;
    public final static int TYPE_DOWN_SMOOTH = 7;
    public final static int TYPE_OPEN_SMOOTH = 8;

    private int currentType = TYPE_NONE;
    private float eventY = 0;
    private int topY;
    private int bottomY;

    private int topControlY1;
    private int bottomControlY1;

    private int topControlY2;
    private int bottomControlY2;


    private int width;
    private int height;

    private Paint mPaint;
    private int position;

    private int mButtonIconSize;
    private int mButtonIconMarginBottom;
    private int mButtonIconMarginTop;
    private int mCrackSize;
    // 到达自动打开的阈值时的偏移量
    private float autoStartX;

    public FlowingMenuLayout(Context context) {
        this(context, null);
    }

    public FlowingMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowingMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mClipPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
        }
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public int getPaintColor() {
        return mPaint.getColor();
    }


    public void setMenuPosition(int position) {
        this.position = position;
    }

    public void setButtonIconSize(int mButtonIconSize) {
        this.mButtonIconSize = mButtonIconSize;
    }

    public void setButtonIconMarginBottom(int mButtonIconMarginBottom) {
        this.mButtonIconMarginBottom = mButtonIconMarginBottom;
    }

    public void setButtonIconMarginTop(int mButtonIconMarginTop) {
        this.mButtonIconMarginTop = mButtonIconMarginTop;
    }

    public void setCrackSize(int crackSize) {
        this.mCrackSize = crackSize;
    }

    /**
     * 设置按钮相对menu的位移
     */
    public void setClipOffsetPixels(float clipOffsetPixels, float eventY, int type) {
        mClipOffsetPixels = clipOffsetPixels;
        currentType = type;
        this.eventY = eventY;
        invalidate();
    }

    /**
     * 设置menu的位移
     */
    public void setMenuOffsetPixels(float clipOffsetPixels, float eventY, int type) {
        mMenuOffsetPixels = clipOffsetPixels;
        currentType = type;
        this.eventY = eventY;
        invalidate();
    }

    public float getMenuOffsetPixels() {
        return mMenuOffsetPixels;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        mClipPath.reset();
        if (position == ElasticPager.Position.LEFT) {
            drawLeftMenu();
        } else {
            drawRightMenu();
        }
        canvas.save();
        canvas.drawPath(mClipPath, mPaint);
        canvas.clipPath(mClipPath, Region.Op.INTERSECT);
        super.dispatchDraw(canvas);
        canvas.restore();

    }

    private void drawLeftMenu() {
        if (mButtonIconMarginTop > 0) {
            mButtonIconMarginBottom = height - mButtonIconMarginTop - mButtonIconSize;
        }
        // 按钮中心纵坐标
        int centerY = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8);

        float offsetLength = width - mButtonIconSize ;
        // 打开状态时偏移
        float offsetOpen = width - mButtonIconSize + mCrackSize ;
        // 正常状态时偏移
        int offsetNone = width + mButtonIconSize - mCrackSize ;
        // 按钮右边缘横坐标
        int centerX = (int) (offsetNone + mClipOffsetPixels);
        switch (currentType) {
            case TYPE_NONE:
                /*
                 * 空状态
                 */
                // 静止时需要保证centerX的正确,重置centerX和autoStartX
                autoStartX = 0;
                centerX = offsetNone;
                topY = (int) (centerY - COEF1 * mButtonIconSize);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize);
                topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);

                topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);

                break;
            case TYPE_UP_MANUAL:
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * mClipOffsetPixels);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * mClipOffsetPixels);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * mClipOffsetPixels);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * mClipOffsetPixels);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;

            case TYPE_UP_AUTO: {
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                // fraction1表示从凸变平的过程
                float fraction1 = Math.max(Math.min(mMenuOffsetPixels / ((1-COEF7) * offsetLength), 1), 0 );
                // fraction2表示从平变凹的过程
                float fraction2 = Math.max(Math.min((mMenuOffsetPixels - (1-COEF7) * offsetLength) / (COEF7 * offsetLength), 1), 0);

                if (mMenuOffsetPixels < (1-COEF7) * offsetLength) {
                    int currentCenterX = (int) (offsetNone + autoStartX);
                    // 当自动打开到1-COEF7的位置时，贝塞尔凸起部分变平
                    int aotuCenterX = (int) (currentCenterX - (currentCenterX - width) * fraction1);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3 + mMenuOffsetPixels);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3 - mMenuOffsetPixels);

                    int r = (int) (mClipOffsetPixels + mMenuOffsetPixels);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * r);


                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                } else {
                    int concaveCenterX = (int) (width - (mButtonIconSize - mCrackSize) * fraction2);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - (1 - fraction2) * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + (1 - fraction2) * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }
                break;
            }
            case TYPE_OPEN: {
                autoStartX = 0;
                int concaveCenterX = (int) offsetOpen;
                topY = (int) (centerY - COEF1 * mButtonIconSize);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize);
                topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);

                topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            }
            case TYPE_DOWN_AUTO:
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                // 从凹到平的过程
                float fraction1 = Math.min(Math.max((mMenuOffsetPixels - COEF7 * offsetLength) / ((1-COEF7) * offsetLength), 0), 1);
                // 从平到凸的过程
                float fraction2 = Math.min(Math.max(mMenuOffsetPixels / (COEF7 * offsetLength), 0),1);
                // 进入TYPE_DOWN_AUTO状态时的初始X
                int currentCenterX = (int) (offsetOpen - (offsetLength - autoStartX));
                int aotuCenterX = (int) (currentCenterX - (currentCenterX - width) * (1 - fraction1));

                if (mMenuOffsetPixels > COEF7 * offsetLength) {
                    // menu的位移
                    float backOffset = width - mCrackSize - mMenuOffsetPixels;
                    float offset = width - mButtonIconSize - mClipOffsetPixels;
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3 + backOffset);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3 - backOffset);
                    int r = (int) (offset + backOffset);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * r);
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                } else {

                    // fraction2=1时 x=width,贝塞尔曲线是平的。fraction2=0时 x= width+mButtonIconSize-mCrackSize,回到初始状态。
                    int concaveCenterX = (int) (offsetNone - (mButtonIconSize - mCrackSize) * fraction2);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - fraction2 * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + fraction2 * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);

                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }
                break;
            case TYPE_DOWN_MANUAL: {
                // width - mButtonIconSize是mClipOffsetPixels的终点
                float offset = width - mButtonIconSize - mClipOffsetPixels;
                int concaveCenterX = (int) (offsetOpen - offset);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * offset);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            }
            case TYPE_DOWN_SMOOTH:
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * mClipOffsetPixels);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * mClipOffsetPixels);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * mClipOffsetPixels);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * mClipOffsetPixels);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);

                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_OPEN_SMOOTH:
                float offset = width - mButtonIconSize - mClipOffsetPixels;
                int concaveCenterX = (int) (offsetOpen - offset);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3);
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * offset);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            default:
                break;
        }
    }

    private void drawRightMenu() {
        if (mButtonIconMarginTop > 0) {
            mButtonIconMarginBottom = height - mButtonIconMarginTop - mButtonIconSize;
        }
        // 按钮中心纵坐标
        int centerY = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8);
        float offsetLength = width  -mButtonIconSize ;
        float offsetOpen = mButtonIconSize - mCrackSize ;
        // 正常状态时偏移
        int offsetNone = -mButtonIconSize + mCrackSize ;
        // 按钮右边缘横坐标
        int centerX = (int) (offsetNone + mClipOffsetPixels);
        switch (currentType) {
            case TYPE_NONE:
                /*
                 * 空状态
                 */
                // 静止时需要保证centerX的正确,重置centerX和autoStartX
                autoStartX = 0;
                centerX = offsetNone;
                topY = (int) (centerY - COEF1 * mButtonIconSize);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize);
                topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);

                topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);
            case TYPE_UP_MANUAL:
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * mClipOffsetPixels);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * mClipOffsetPixels);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * mClipOffsetPixels);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * mClipOffsetPixels);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_UP_AUTO: {
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                float fraction1 = Math.max(Math.min(-mMenuOffsetPixels / ((1-COEF7) * offsetLength), 1), 0);
                float fraction2 = Math.max(Math.min((-mMenuOffsetPixels - (1-COEF7) * offsetLength) / (COEF7 * offsetLength), 1),0);

                if (-mMenuOffsetPixels < (1-COEF7)  * offsetLength) {
                    int currentCenterX = (int) (- mButtonIconSize + mCrackSize + autoStartX);
                    int aotuCenterX = (int) (currentCenterX - currentCenterX * fraction1);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3 - mMenuOffsetPixels);
                    topY = (int) (centerY - COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3 + mMenuOffsetPixels);
                    int r = (int) (mClipOffsetPixels + mMenuOffsetPixels);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * r);

                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(0, 0);
                    break;
                }else {

                    int concaveCenterX = (int) ( offsetOpen * fraction2);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - (1 - fraction2) * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + (1 - fraction2) * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(0, 0);
                }
            }

            case TYPE_OPEN: {
                autoStartX = 0;
                int concaveCenterX = (int) offsetOpen;
                topY = (int) (centerY - COEF1 * mButtonIconSize);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize);
                topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);

                break;
            }
            case TYPE_DOWN_AUTO:
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                float fraction1 = Math.min(Math.max((-mMenuOffsetPixels - COEF7 * offsetLength) / ((1-COEF7) * offsetLength), 0), 1);
                float fraction2 = Math.min(Math.max(-mMenuOffsetPixels / (COEF7 * offsetLength), 0), 1);


                int currentCenterX = (int) (offsetOpen + (offsetLength + autoStartX));
                int aotuCenterX = (int) (currentCenterX - currentCenterX  * (1 - fraction1));

                if (-mMenuOffsetPixels > COEF7 * offsetLength) {
                    float backOffset = width - mCrackSize - (-mMenuOffsetPixels);
                    float offset = width - mButtonIconSize - (-mClipOffsetPixels);

                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3 + backOffset);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3 - backOffset);
                    int r = (int) (offset + backOffset);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * r);

                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(0, 0);
                } else {

                    int concaveCenterX = (int) (offsetNone - offsetNone * fraction2);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - fraction2 * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + fraction2 * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(0, 0);
                }
                break;
            case TYPE_DOWN_MANUAL: {
                // -width + mButtonIconSize是mClipOffsetPixels的终点
                float offset =  -width + mButtonIconSize - mClipOffsetPixels;
                int concaveCenterX = (int) (offsetOpen - offset);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + offset * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * offset);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);
                break;
            }
            case TYPE_DOWN_SMOOTH:
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - mClipOffsetPixels * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + mClipOffsetPixels * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * mClipOffsetPixels);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * mClipOffsetPixels);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * mClipOffsetPixels);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * mClipOffsetPixels);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_OPEN_SMOOTH:

                float offset =  -width + mButtonIconSize - mClipOffsetPixels;
                int concaveCenterX = (int) (offsetOpen - offset);
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + offset * COEF3);
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * offset);

                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                        topControlY1, concaveCenterX, centerY);
                mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(0, 0);
                break;
            default:
                break;
        }
    }

}
