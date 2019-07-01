package com.hb.coop.ui.vehicle

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.TRANSITION_TOOLBAR
import com.hb.coop.data.entity.DataWrapper
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.hb.uiwidget.recyclerview.EndlessRecyclerViewListener
import com.hb.uiwidget.recyclerview.OnItemClickListener

class VehicleActivity : HBMvpLceSRActivity<List<DataWrapper<*>>, VehiclePresenter>(), VehicleContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_lce_sr_search
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.text_view_total)
    lateinit var totalView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.transitionName = TRANSITION_TOOLBAR

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)

            actionBar.title = "Xe Vận Chuyển"
        }

        totalView.visibility = View.GONE
    }

    override fun setTotal(total: Int) {
        val text = "Số lượng: $total"
        totalView.text = text
        totalView.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setupRecylcerView(addItemDecoration: Boolean) {
        super.setupRecylcerView(addItemDecoration)
        val rv = getRecyclerView()
        rv.addOnScrollListener(object : EndlessRecyclerViewListener() {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                mPresenter.loadNextPage()
            }
        })
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = VehicleAdapter(context, recyclerView)
        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->

        })
        return adapter
    }

    override fun setData(data: List<DataWrapper<*>>) {
        val adapter = getAdapter<VehicleAdapter>()
        adapter.data = data
    }

    class VehicleViewHolder(itemView: View) : BaseViewHolder<DataWrapper<*>>(itemView) {

        @BindView(R.id.text_view_plate_number)
        lateinit var plateNumber: TextView
        @BindView(R.id.text_view_driver_name)
        lateinit var driverName: TextView
        @BindView(R.id.text_view_transport_code)
        lateinit var code: TextView
        @BindView(R.id.text_view_basket_count)
        lateinit var basketCount: TextView
        @BindView(R.id.text_view_transport_state)
        lateinit var state: TextView

        override fun bindData(data: DataWrapper<*>) {
            plateNumber.text = data.getTitle()
        }
    }

    class VehicleAdapter(
        context: Context,
        recyclerView: RecyclerView
    ) : BaseAdapter<List<DataWrapper<*>>, VehicleViewHolder>(context, recyclerView) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_transport, parent, false)
            return VehicleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<DataWrapper<*>>(position)!!
            holder.bindData(data)
        }

        override fun getItemCount(): Int {
            if (mData == null)
                return 0
            return mData!!.size
        }
    }
}