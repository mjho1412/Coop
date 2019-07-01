package com.hb.coop.ui.image_viewer;

import java.util.List;

/**
 * Created by buihai on 5/24/17.
 */

public interface TouchImageViewContract {

    interface View {
        void showLoading();

        void showContent();

        void showError();

        void setData(List<String> images, int position);
    }

    interface Presenter {
        void loadAllImages();
    }

    String IMAGES_TEST_TAG = "IMAGES_TEST";

}
