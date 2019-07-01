package com.hb.coop.ui.test.order

import android.os.Build
import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.AppConstants
import com.hb.coop.common.TRANSITION_CARD
import com.hb.coop.data.entity.DataWrapper
import com.hb.uiwidget.recyclerview.BaseViewHolder
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class OrderViewHolder(itemView: View) : BaseViewHolder<DataWrapper<String>>(itemView), LayoutContainer {

    @BindView(R.id.tv_title)
    lateinit var titleView: TextView
    @BindView(R.id.tv_title_date)
    lateinit var dateView: TextView
    @BindView(R.id.tv_description)
    lateinit var descriptionView: TextView

    override fun bindData(data: DataWrapper<String>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            containerView?.transitionName = TRANSITION_CARD + adapterPosition
        }

        titleView.text = data.getTitle()
        dateView.text = AppConstants.formatDate.format(Date())
        descriptionView.text = data.getDescription()


    }

    override val containerView: View?
        get() = itemView
}