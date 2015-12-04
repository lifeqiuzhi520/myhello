package com.camera.www.camera;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Administrator on 2015/12/4.
 * we need know that 十二月
 */
public class BaseAct extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public <T extends View> T findView(int resId) {
        return (T) findViewById(resId);

    }
}
