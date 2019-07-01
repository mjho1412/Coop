package com.hb.coop.ui.image_viewer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import com.hb.coop.R;
import com.hb.lib.mvp.impl.HBMvpActivity;
import com.hb.uiwidget.viewpager.ExtendedViewPager;

import java.util.List;

/**
 * Created by buihai on 5/24/17.
 */

public class TouchImageViewActivity extends HBMvpActivity<TouchImageViewPresenter>
        implements TouchImageViewContract.View {

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, TouchImageViewActivity.class);
        return intent;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_touch_image_view;
    }


    @BindView(R.id.viewpager_view_image)
    ExtendedViewPager mViewPager;
    TouchImageAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new TouchImageAdapter();
        mViewPager.setAdapter(mAdapter);

        mPresenter.loadAllImages();


        getView().setOnClickListener(v -> {
            onClose();
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void setData(List<String> images, int position) {

//        if (images == null) {
//            images = new ArrayList<>();
//            images.add(getIntent().getStringExtra("IMAGES_TEST"));
//        }

        mAdapter.setData(images);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(position);
    }

    @OnClick(R.id.image_view_close)
    public void onClose() {
        finish();
    }

}
