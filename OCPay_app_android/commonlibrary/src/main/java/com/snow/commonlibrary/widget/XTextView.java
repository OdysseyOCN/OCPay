package com.snow.commonlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.snow.commonlibrary.R;


/**
 * Created by Administrator on 2017/9/4.
 */

public class XTextView extends AppCompatTextView {

    private Drawable leftDrawable, topDrawable, rightDrawable, bottomDrawable;  //左，顶，右，底图标
    private int leftDrawableWidth, leftDrawableHeight;  //左图标宽高
    private int topDrawableWidth, topDrawableHeight;  //顶图标宽高
    private int rightDrawableWidth, rightDrawableHeight;  //右图标宽高
    private int bottomDrawableWidth, bottomDrawableHeight;  //底图标宽高

    private final int DEFAULT_SIZE = -0x1;
    private int drawableMarginBottom;
    private int drawableMarginTop;

    public XTextView(Context context) {
        this(context, null, 0);
    }

    public XTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        drawDrawable(leftDrawable, topDrawable, rightDrawable, bottomDrawable);

    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.XTextView);

        leftDrawable = array.getDrawable(R.styleable.XTextView_leftDrawable);
        leftDrawableWidth = array.getDimensionPixelSize(R.styleable.XTextView_leftDrawableWidth, DEFAULT_SIZE);
        leftDrawableHeight = array.getDimensionPixelSize(R.styleable.XTextView_leftDrawableHeight, DEFAULT_SIZE);

        topDrawable = array.getDrawable(R.styleable.XTextView_topDrawable);
        topDrawableWidth = array.getDimensionPixelSize(R.styleable.XTextView_topDrawableWidth, DEFAULT_SIZE);
        topDrawableHeight = array.getDimensionPixelSize(R.styleable.XTextView_topDrawableHeight, DEFAULT_SIZE);

        rightDrawable = array.getDrawable(R.styleable.XTextView_rightDrawable);
        rightDrawableWidth = array.getDimensionPixelSize(R.styleable.XTextView_rightDrawableWidth, DEFAULT_SIZE);
        rightDrawableHeight = array.getDimensionPixelSize(R.styleable.XTextView_rightDrawableHeight, DEFAULT_SIZE);

        bottomDrawable = array.getDrawable(R.styleable.XTextView_bottomDrawable);
        bottomDrawableWidth = array.getDimensionPixelSize(R.styleable.XTextView_bottomDrawableWidth, DEFAULT_SIZE);
        bottomDrawableHeight = array.getDimensionPixelSize(R.styleable.XTextView_bottomDrawableHeight, DEFAULT_SIZE);

        drawableMarginBottom = array.getDimensionPixelSize(R.styleable.XTextView_drawable_margin_bottom, 0);
        drawableMarginTop = array.getDimensionPixelSize(R.styleable.XTextView_drawable_margin_top, 0);

        array.recycle();
    }

    /**
     * 重新测量图标的宽度
     *
     * @param drawable      图标
     * @param drawableWidth 图标宽度
     * @return
     */
    private int retestDrawableWidth(Drawable drawable, int drawableWidth) {
        if (drawable != null) {
            if (drawableWidth == DEFAULT_SIZE) {
                drawableWidth = drawable.getIntrinsicWidth();
            }
            return drawableWidth;
        }
        return 0;
    }

    /**
     * 重新测量图标的高度
     *
     * @param drawable       图标
     * @param drawableHeight 图标高度
     * @return
     */
    private int retestDrawableHeight(Drawable drawable, int drawableHeight) {
        if (drawable != null) {
            if (drawableHeight == DEFAULT_SIZE) {
                drawableHeight = drawable.getIntrinsicHeight();
            }
            return drawableHeight;
        }
        return 0;
    }

    /**
     * 绘制图标
     *
     * @param leftDrawable   左图标
     * @param topDrawable    顶图标
     * @param rightDrawable  右图标
     * @param bottomDrawable 底图标
     */
    public void drawDrawable(Drawable leftDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottomDrawable) {
        if (leftDrawable != null) {
            leftDrawable.setBounds(0, 0 + drawableMarginTop, retestDrawableWidth(leftDrawable, leftDrawableWidth),
                    retestDrawableHeight(leftDrawable, leftDrawableHeight) + drawableMarginTop);
        }
        if (topDrawable != null) {
            topDrawable.setBounds(0, 0 + drawableMarginTop, retestDrawableWidth(topDrawable, topDrawableWidth),
                    retestDrawableHeight(topDrawable, topDrawableHeight) + drawableMarginTop);
        }
        if (rightDrawable != null) {
            rightDrawable.setBounds(0, 0 + drawableMarginTop, retestDrawableWidth(rightDrawable, rightDrawableWidth),
                    retestDrawableHeight(rightDrawable, rightDrawableHeight) + drawableMarginTop);
        }
        if (bottomDrawable != null) {
            bottomDrawable.setBounds(0, 0 + drawableMarginTop, retestDrawableWidth(bottomDrawable, bottomDrawableWidth),
                    retestDrawableHeight(bottomDrawable, bottomDrawableHeight) + drawableMarginTop);
        }

        setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
    }

    /**
     * 设置左图标宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setLeftDrawableSize(int width, int height) {
        leftDrawableWidth = width;
        leftDrawableHeight = height;
    }

    /**
     * 设置顶图标宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setTopDrawableSize(int width, int height) {
        topDrawableWidth = width;
        topDrawableHeight = height;
    }

    /**
     * 设置右图标宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setRightDrawableSize(int width, int height) {
        rightDrawableWidth = width;
        rightDrawableHeight = height;
    }

    /**
     * 设置底图标宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setBottomDrawableSize(int width, int height) {
        bottomDrawableWidth = width;
        bottomDrawableHeight = height;
    }


    public void setLeftDrawable(Drawable leftDrawable) {
        this.leftDrawable = leftDrawable;
        drawDrawable(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        postInvalidate();

    }

    public void setTopDrawable(Drawable topDrawable) {
        this.topDrawable = topDrawable;
        drawDrawable(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        postInvalidate();

    }

    public void setRightDrawable(Drawable rightDrawable) {
        this.rightDrawable = rightDrawable;
        drawDrawable(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        postInvalidate();

    }

    public void setBottomDrawable(Drawable bottomDrawable) {
        this.bottomDrawable = bottomDrawable;
        drawDrawable(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        postInvalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }


    @Override
    public void postInvalidate() {
        super.postInvalidate();

    }
}
