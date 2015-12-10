package com.camera.www.camera.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.camera.www.camera.R;

public class BrightAjustView extends ViewGroup {
    private ImageView mImageView;
    private float originX,originY;

    public BrightAjustView(Context context) {
        this(context, null);
    }

    public BrightAjustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_bright_ajust, this);
        mImageView = (ImageView) view.findViewById(R.id.light_ajust_dot);
        mImageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mImageView.scrollTo((int)event.getX(),(int) event.getY());
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).layout(l, t, r, b);
        }
    }
}
