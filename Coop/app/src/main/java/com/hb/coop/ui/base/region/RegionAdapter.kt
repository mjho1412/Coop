package com.hb.coop.ui.base.region

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hb.coop.R
import com.hb.coop.data.entity.Region
import com.hb.uiwidget.recyclerview.BaseAdapter

class RegionAdapter(context: Context, rv: RecyclerView) : BaseAdapter<List<Region>, RegionViewHolder>(context, rv) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val itemView = mInflater.inflate(R.layout.item_view_region, parent, false)
        val vHolder = RegionViewHolder(itemView)
        return vHolder
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val data = getItem<Region>(position)!!
        holder.bindData(data)

    }

    override fun getItemCount(): Int {
        if (mData == null)
            return 0
        return mData!!.size
    }
}