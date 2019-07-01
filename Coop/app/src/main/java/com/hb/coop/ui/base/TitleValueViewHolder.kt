package com.hb.coop.ui.base

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.hb.coop.R
import com.hb.uiwidget.recyclerview.BaseViewHolder

class TitleValueViewHolder(itemView: View) : BaseViewHolder<Pair<String, String>>(itemView) {

    @BindView(R.id.text_view_value)
    lateinit var valueView: TextView
    @BindView(R.id.text_view_title)
    lateinit var titleView: TextView

    override fun bindData(data: Pair<String, String>) {

        titleView.text = data.first
        valueView.text = data.second
    }
}