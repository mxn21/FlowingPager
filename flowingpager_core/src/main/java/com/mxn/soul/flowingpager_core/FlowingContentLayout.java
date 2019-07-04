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

import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_DOWN_AUTO;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_DOWN_MANUAL;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_DOWN_SMOOTH;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_NONE;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_OPEN;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_OPEN_SMOOTH;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_UP_AUTO;
import static com.mxn.soul.flowingpager_core.FlowingMenuLayout.TYPE_UP_MANUAL;
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
 * FlowingContentLayout
 */

@SuppressWarnings("FieldCanBeLocal")
public class FlowingContentLayout extends FrameLayout {

    private Path mClipPath;
    private float mClipOffsetPixels = 0;
    private float mContentOffsetPixels = 0;

    private int currentType = TYPE_NONE;
    private int topY;
    private int bottomY;
    private int topControlY1 ;
    private int bottomControlY1 ;
    private int topControlY2  ;
    private int bottomControlY2 ;

    private int width;
    private int height;

    private Paint mPaint;
    private int position;

    private int mButtonIconSize ;
    private int mButtonIconMarginBottom ;
    private int mButtonIconMarginTop ;
    private int mCrackSize ;
    private float autoStartX ;
    private float eventY ;

    public FlowingContentLayout(Context context) {
        this(context, null);
    }

    public FlowingContentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowingContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setClipOffsetPixels(float clipOffsetPixels, float eventY, int type) {
        mClipOffsetPixels = clipOffsetPixels;
        currentType = type;
        this.eventY = eventY;
        invalidate();
    }

    public void setContentOffsetPixels(float clipOffsetPixels, float eventY, int type) {
        mContentOffsetPixels = clipOffsetPixels;
        currentType = type;
        this.eventY = eventY;
        invalidate();
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

        int centerY = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8);
        // 打开状态时mClipOffsetPixels = width - mButtonIconSize,contentView在下层，不用考虑关闭状态
        int centerX = (int) (- mButtonIconSize + mCrackSize - (width - mButtonIconSize - mClipOffsetPixels));
        float offsetLength = width - mButtonIconSize ;
        switch (currentType) {
            case TYPE_NONE:
                autoStartX = 0 ;
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo( width , 0);
                mClipPath.lineTo( width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_UP_MANUAL: {
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo( width , 0);
                mClipPath.lineTo( width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            }
            case TYPE_UP_AUTO: {
                // mContentOffsetPixels需要多减去一个mCrackSize才能跟mMenuOffsetPixels相等
                if (mContentOffsetPixels - mCrackSize - (1-COEF7) * offsetLength > 0) {
                    float fraction = Math.max(Math.min((mContentOffsetPixels - mCrackSize - (1-COEF7) * offsetLength) / (COEF7 * offsetLength), 1),0);
                    int concaveCenterX = (int) ((- mButtonIconSize + mCrackSize) * fraction);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - (1 - fraction) * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + (1 - fraction) * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);
                    mClipPath.moveTo(width, 0);
                    mClipPath.lineTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                } else {
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo( width , 0);
                    mClipPath.lineTo( width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }
                break;
            }
            case TYPE_OPEN:
                autoStartX = 0 ;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize );
                topY = (int) (centerY - COEF1 * mButtonIconSize );
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 );
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 );
                topControlY2 = (int) (topY + mButtonIconSize * COEF5  );
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 );
                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                break ;
            case TYPE_DOWN_AUTO:
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                // 多减去一个mCrackSize才能和mMenuOffsetPixels的fraction相等
                float fraction = Math.min(Math.max((mContentOffsetPixels - mCrackSize- COEF7 * offsetLength) / ((1-COEF7) * offsetLength), 0),1);
                // 参考CenterX的定义，mClipOffsetPixels替换成autoStartX
                int currentCenterX =(int) (- mButtonIconSize + mCrackSize - (width - mButtonIconSize - autoStartX));
                int aotuCenterX = (int) (currentCenterX - currentCenterX * (1-fraction));
                if (mContentOffsetPixels > COEF7 * offsetLength ) {
                    float offset = width - mButtonIconSize - mClipOffsetPixels ;
                    float backOffset = width -   mCrackSize - mContentOffsetPixels ;
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3 + backOffset);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3 - backOffset);
                    int r = (int) (offset + backOffset);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * r);
                    mClipPath.moveTo(width, 0);
                    mClipPath.lineTo(0, 0);
                    mClipPath.lineTo(0, topY);
                    mClipPath.cubicTo(0, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, 0,
                            bottomControlY2, 0, bottomY);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(width, 0);
                } else {
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo( width , 0);
                    mClipPath.lineTo( width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }

                break;
            case TYPE_DOWN_MANUAL: {
                float offset = width - mButtonIconSize - mClipOffsetPixels ;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * offset);

                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);

                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);

                break;
            }
            case TYPE_DOWN_SMOOTH:
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo( width , 0);
                mClipPath.lineTo( width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_OPEN_SMOOTH:
                float offset = width -  mButtonIconSize - mClipOffsetPixels ;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3);
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * offset);

                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(0, topY);
                mClipPath.cubicTo(0, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, 0,
                        bottomControlY2, 0, bottomY);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(width, 0);
                break;
            default:
                break;
        }
    }


    private void drawRightMenu() {
        if (mButtonIconMarginTop > 0) {
            mButtonIconMarginBottom = height - mButtonIconMarginTop - mButtonIconSize;
        }

        int centerY = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8);
        // 打开状态时mClipOffsetPixels = width - mButtonIconSize
        int centerX = (int) (width + mButtonIconSize - mCrackSize - (-width + mButtonIconSize - mClipOffsetPixels));
        float offsetLength = width - mButtonIconSize ;
        switch (currentType) {
            case TYPE_NONE:
                autoStartX = 0;
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_UP_MANUAL: {
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            }
            case TYPE_UP_AUTO: {
                if (-mContentOffsetPixels -mCrackSize - (1-COEF7) * offsetLength > 0) {
                    float fraction = Math.max(Math.min((-mContentOffsetPixels -mCrackSize - (1-COEF7) * offsetLength) / (COEF7 * offsetLength), 1) , 0) ;
                    int concaveCenterX = (int) (width + (mButtonIconSize - mCrackSize) * fraction);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - (1 - fraction) * mButtonIconSize);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + (1 - fraction) * mButtonIconSize);
                    topControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 - mButtonIconSize * COEF2);
                    bottomControlY1 = (int) (height - mButtonIconMarginBottom - mButtonIconSize * COEF8 + mButtonIconSize * COEF2);
                    topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                    bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                    mClipPath.moveTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, concaveCenterX,
                            topControlY1, concaveCenterX, centerY);
                    mClipPath.cubicTo(concaveCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                    mClipPath.lineTo(width, 0);
                } else {
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }
                break;
            }
            case TYPE_OPEN:
                autoStartX = 0;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize);
                topY = (int) (centerY - COEF1 * mButtonIconSize);
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5);

                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(width, 0);
                break;
            case TYPE_DOWN_AUTO:
                if (autoStartX == 0) {
                    autoStartX = mClipOffsetPixels;
                }
                // 多减去一个mCrackSize，使fraction和menu的fraction1相同
                float fraction = Math.min(Math.max((-mContentOffsetPixels - mCrackSize- COEF7 * offsetLength) / ((1 - COEF7) * offsetLength), 0) ,1);
                // 参考CenterX的定义，mClipOffsetPixels替换成autoStartX
                int currentCenterX = (int) (width + mButtonIconSize - mCrackSize - (-width + mButtonIconSize - autoStartX));
                int aotuCenterX = (int) (currentCenterX + (width - currentCenterX) * (1 - fraction));
                if (-mContentOffsetPixels > COEF7 * offsetLength) {
                    float offset = width - mButtonIconSize - (-mClipOffsetPixels);
                    float backOffset = width - mCrackSize - (-mContentOffsetPixels);
                    bottomY = (int) (centerY + COEF1 * mButtonIconSize + offset * COEF3 + backOffset);
                    topY = (int) (centerY - COEF1 * mButtonIconSize - offset * COEF3 - backOffset);
                    int r = (int) (offset + backOffset);
                    int topControlY1 = (int) (centerY - mButtonIconSize * COEF2 - COEF4 * r);
                    int bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 + COEF4 * r);
                    int topControlY2 = (int) (topY + mButtonIconSize * COEF5 + COEF6 * r);
                    int bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 - COEF6 * r);
                    mClipPath.moveTo(width, 0);
                    mClipPath.lineTo(width, topY);
                    mClipPath.cubicTo(width, topControlY2, aotuCenterX,
                            topControlY1, aotuCenterX, centerY);
                    mClipPath.cubicTo(aotuCenterX, bottomControlY1, width,
                            bottomControlY2, width, bottomY);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                    mClipPath.lineTo(width, 0);
                } else {
                    mClipPath.moveTo(0, 0);
                    mClipPath.lineTo(width, 0);
                    mClipPath.lineTo(width, height);
                    mClipPath.lineTo(0, height);
                    mClipPath.lineTo(0, 0);
                }

                break;
            case TYPE_DOWN_MANUAL: {
                float offset = -width + mButtonIconSize - mClipOffsetPixels;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + offset * COEF3);

                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * offset);

                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(width, 0);

                break;
            }
            case TYPE_DOWN_SMOOTH:
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(width, 0);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                break;
            case TYPE_OPEN_SMOOTH:
                float offset = -width + mButtonIconSize - mClipOffsetPixels;
                bottomY = (int) (centerY + COEF1 * mButtonIconSize - offset * COEF3);
                topY = (int) (centerY - COEF1 * mButtonIconSize + offset * COEF3);
                topControlY1 = (int) (centerY - mButtonIconSize * COEF2 + COEF4 * offset);
                bottomControlY1 = (int) (centerY + mButtonIconSize * COEF2 - COEF4 * offset);
                topControlY2 = (int) (topY + mButtonIconSize * COEF5 - COEF6 * offset);
                bottomControlY2 = (int) (bottomY - mButtonIconSize * COEF5 + COEF6 * offset);

                mClipPath.moveTo(width, 0);
                mClipPath.lineTo(width, topY);
                mClipPath.cubicTo(width, topControlY2, centerX,
                        topControlY1, centerX, centerY);
                mClipPath.cubicTo(centerX, bottomControlY1, width,
                        bottomControlY2, width, bottomY);
                mClipPath.lineTo(width, height);
                mClipPath.lineTo(0, height);
                mClipPath.lineTo(0, 0);
                mClipPath.lineTo(width, 0);
                break;
            default:
                break;
        }
    }

}
