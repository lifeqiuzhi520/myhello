package com.camera.www.camera;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.camera.www.camera.activity.activity.TakePhotoAct;
import com.camera.www.camera.custom_view.PointIndicateView;

public class MainActivity extends BaseAct implements View.OnClickListener {
    /**
     * 拍照，相册，图鉴，拼图，扫描卡牌
     */

    private View mTakePhoto, mPhoto, mFieldGuide, mPuzzle, mScanCard;
    private ViewPager mBannerViewPager;
    private PointIndicateView mIndicateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListener();
    }

    private void initViews() {
        mTakePhoto = findView(R.id.take_picture);
        mPhoto = findView(R.id.photo);
        mFieldGuide = findView(R.id.field_guide);
        mPuzzle = findView(R.id.puzzle);
        mScanCard = findView(R.id.scan_card);
        mBannerViewPager = findView(R.id.menu_view_pager);
        mIndicateView = findView(R.id.indicate);
    }

    private void initListener() {
        mTakePhoto.setOnClickListener(this);
        mPhoto.setOnClickListener(this);
        mFieldGuide.setOnClickListener(this);
        mPuzzle.setOnClickListener(this);
        mScanCard.setOnClickListener(this);
    }

    /**
     * 拍照
     */
    private void startTakePhoto() {
        startActivity(TakePhotoAct.class);

    }

    /**
     * 查看图片
     */
    private void startScanPhoto() {

    }

    /**
     * 开始图鉴
     */
    private void startFieldGuide() {

    }

    /**
     * 开始拼图
     */
    private void startPuzzle() {

    }

    /**
     * 扫描卡牌
     */
    private void startScanCard() {

    }

    @Override
    public void onClick(View v) {
        if (v == mTakePhoto) {
            startTakePhoto();
        } else if (v == mPhoto) {
            startScanPhoto();
        } else if (v == mFieldGuide) {
            startFieldGuide();
        } else if (v == mPuzzle) {
            startPuzzle();
        } else if (v == mScanCard) {
            startScanCard();
        }

    }
}
