package com.camera.www.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Administrator on 2015/12/4.
 * we need know that 十二月
 */
public class BaseAct extends FragmentActivity {
    protected  String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getLocalClassName();
        super.onCreate(savedInstanceState);
    }

    public <T extends View> T findView(int resId) {
        return (T) findViewById(resId);

    }
    protected   void startActivity(Class<?> cls) {

        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
