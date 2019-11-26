package com.nemo.wxpay_library.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {
    private Context mContext;
    private int mWidth = 30;
    private int mHeight = 30;

    //保存设置的图片
    private Drawable mLeft, mTop, mRight, mBottom;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        float density = mContext.getResources().getDisplayMetrics().density;
        //将dip转化成px
        mWidth *= density;
        mHeight *= density;
        //手动调用该方法，设置文字上下左右方向的图片宽高
        setCompoundDrawablesWithIntrinsicBounds(mLeft, mTop, mRight, mBottom);//设置图片的宽高
    }

    /**
     * 重写该方法，文字上下左右方向的图片大小设置
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, mWidth, mHeight);
            mLeft = left;
        }
        if (right != null) {
            right.setBounds(0, 0, mWidth, mHeight);
            mRight = right;
        }
        if (top != null) {
            top.setBounds(0, 0, mWidth, mHeight);
            mTop = top;
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mWidth, mHeight);
            mBottom = bottom;
        }
        setCompoundDrawables(left, top, right, bottom);//设置图片的宽高
    }

    /**
     * 设置图片的宽高
     *
     * @param width
     * @param height
     */
    public void setBounds(int width, int height) {
        mWidth = width;
        mHeight = height;
        init();
    }

}