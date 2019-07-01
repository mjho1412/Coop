package com.hb.coop.ui.test.ja;

import com.hb.coop.R;
import com.hb.lib.mvp.impl.HBMvpActivity;

public class PassportXActivity extends HBMvpActivity<PassportXPresenter> implements PassportXContract.View {

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_passport;
    }


}
