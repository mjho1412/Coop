package com.hb.coop.ui.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.hb.coop.R
import com.hb.coop.app.App
import com.hb.coop.data.entity.Passport
import com.hb.coop.navigation.Navigator
import com.hb.uiwidget.recyclerview.BaseViewHolder

class NavHeaderViewHolder(itemView: View) : BaseViewHolder<Passport>(itemView) {

    @BindView(R.id.image_view_nav_header_background)
    lateinit var backgroundView: ImageView
    @BindView(R.id.image_view_nav_header_avatar)
    lateinit var avatarView: ImageView
    @BindView(R.id.text_view_nav_header_full_name)
    lateinit var fullNameView: TextView
    @BindView(R.id.text_view_nav_header_email)
    lateinit var emailView: TextView

    override fun bindData(data: Passport) {

        App.imageHelper.loadBanner(backgroundView, data.config.backgroundUrl)
        App.imageHelper.loadAvatar(avatarView, data.avatarUrl)

        fullNameView.text = data.fullName
        emailView.text = data.email


    }

    @OnClick(R.id.view_group_nav_header_info)
    fun onProfile() {
        Navigator.startProfile(App.getInstance<App>().currentActivity)
    }
}