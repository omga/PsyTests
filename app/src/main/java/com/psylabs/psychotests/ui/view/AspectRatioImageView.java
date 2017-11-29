package com.psylabs.psychotests.ui.view;

import android.content.Context;
import android.util.AttributeSet;

public class AspectRatioImageView extends android.support.v7.widget.AppCompatImageView {

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getDrawable() == null)
            return;
        int width = getMeasuredWidth();
        int intrinsicWidth = getDrawable().getIntrinsicWidth();
        if(intrinsicWidth == 0)
            intrinsicWidth = 1;
        int height = width * getDrawable().getIntrinsicHeight() / intrinsicWidth;

        setMeasuredDimension(width, height);
    }

}