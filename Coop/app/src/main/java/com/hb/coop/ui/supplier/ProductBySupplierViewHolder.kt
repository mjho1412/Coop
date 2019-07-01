package com.hb.coop.ui.supplier

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.TRANSITION_CARD
import com.hb.coop.common.supportsLollipop
import com.hb.uiwidget.recyclerview.BaseViewHolder

class ProductBySupplierViewHolder(itemView: View) : BaseViewHolder<ProductWrapper>(itemView) {

    @BindView(R.id.tv_title)
    lateinit var titleView: TextView
    @BindView(R.id.tv_subtitle)
    lateinit var subtitleView: TextView
    @BindView(R.id.tv_description)
    lateinit var descriptionView: TextView
    @BindView(R.id.tv_title_right)
    lateinit var titleRightView: TextView

    override fun bindData(data: ProductWrapper) {
        titleView.text = data.getTitle()
        subtitleView.text = data.getSubtitle()
        descriptionView.text = HtmlCompat.fromHtml(data.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)

        titleRightView.text = data.getIcon()

        supportsLollipop {
            itemView.transitionName = TRANSITION_CARD + adapterPosition
        }
    }
}