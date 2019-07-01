package com.hb.coop.ui.test.ja;

import com.hb.coop.data.repository.SuperMarketRepository;
import com.hb.lib.mvp.impl.HBMvpPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;

public class PassportXPresenter extends HBMvpPresenter<PassportXActivity> implements PassportXContract.Presenter {

    private SuperMarketRepository repository;

    @Inject
    public PassportXPresenter(SuperMarketRepository repository) {
        this.repository = repository;
    }

    @Override
    public void loadData() {
        disposable.clear();
        disposable.add(
                repository.getSuperMarket(1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                        }, error -> {

                        })
        );
    }
}
