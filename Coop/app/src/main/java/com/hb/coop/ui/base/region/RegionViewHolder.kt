package com.hb.coop.ui.base.region

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.data.entity.Region
import com.hb.coop.data.entity.Ward
import com.hb.uiwidget.recyclerview.BaseViewHolder

class RegionViewHolder(itemView: View) : BaseViewHolder<Region>(itemView) {

    @BindView(R.id.text_view_title)
    lateinit var titleView: TextView

    @BindView(R.id.text_view_subtitle)
    lateinit var subtitleView: TextView

    @BindView(R.id.text_view_gasoline)
    lateinit var gasolineView: TextView

    @BindView(R.id.text_view_medical)
    lateinit var medicalView: TextView

    override fun bindData(data: Region) {

        titleView.text = data.getName()

        val region = data.getParent()
        if (region != null) {
            subtitleView.text = region.getName()
        } else {
            if (data is Ward) {
                subtitleView.text = data.districtName
            }
        }
        gasolineView.text = "${data.getGasoline()}"
        medicalView.text = "${data.getMedical()}"


    }
}