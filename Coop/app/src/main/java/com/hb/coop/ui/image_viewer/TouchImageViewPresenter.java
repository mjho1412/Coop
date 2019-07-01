package com.hb.coop.ui.image_viewer;


import com.hb.lib.mvp.impl.HBMvpPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by buihai on 5/24/17.
 */

public class TouchImageViewPresenter extends HBMvpPresenter<TouchImageViewActivity> implements TouchImageViewContract.Presenter {

    private int mCurrentPosition = -1;

    @Inject
    public TouchImageViewPresenter() {
    }

    @Override
    public void loadAllImages() {

        mCurrentPosition = -1;

        Observable<List<String>> observable = Observable.create(subscribe -> {

            try {
                List<String> arr = new ArrayList<>(10);
                String data = getView().getIntent().getStringExtra("IMAGES_TEST");
                String[] arrTemp = data.split(Pattern.quote("|"));
                for (int i = 1; i < arrTemp.length; i++) {
                    arr.add(arrTemp[i]);
                }
                mCurrentPosition = Integer.valueOf(arrTemp[0]);
                subscribe.onNext(arr);
            } catch (Exception e) {
                subscribe.onError(e.getCause());
            }
        });

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (isViewAttached()) {
                        getView().setData(data, mCurrentPosition);
                    }
                }, throwable -> {

                });

    }
}
