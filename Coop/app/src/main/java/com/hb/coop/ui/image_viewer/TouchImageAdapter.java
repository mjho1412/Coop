package com.hb.coop.ui.image_viewer;


import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import com.hb.coop.app.App;
import com.hb.uiwidget.TouchImageView;

import java.util.List;

/**
 * Created by buihai on 5/24/17.
 */

public class TouchImageAdapter extends PagerAdapter {

    private List<String> mImages;

    public void setData(List<String> images) {
        mImages = images;
    }

    @Override
    public int getCount() {
        if (mImages == null)
            return 0;
        return mImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView img = new TouchImageView(container.getContext());
//        img.setImageResource(
//                mImages[iconResId]);
        String imageStr = getImage(position);
        if (isUrl(position)) {
            App.imageHelper.loadImageFromUrl(img, imageStr, true);
        } else {
            App.imageHelper.loadImageFromUrl(img, imageStr, true);
        }
        container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return img;
    }

    String getImage(int position) {
        String temp = mImages.get(position);
        String[] arr = temp.split(";");
        return arr[0];
    }

    boolean isUrl(int position) {
        String temp = mImages.get(position);
        String[] arr = temp.split(";");
        if (arr.length < 2)
            return true;
        return Boolean.parseBoolean(arr[1]);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, iconResId, object);
        container.removeView((View) object);
    }
}
