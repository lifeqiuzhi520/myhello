package com.camera.www.camera.interfaces;

import com.camera.www.camera.custom_view.ObservableScrollView;

public interface ScrollViewListener {

    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

}
