package com.hb.coop.ui.supplier

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.TRANSITION_TOOLBAR
import com.hb.coop.navigation.Navigator
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.EndlessRecyclerViewListener
import com.hb.uiwidget.recyclerview.OnItemClickListener

class SupplierActivity : HBMvpLceSRActivity<List<ProductWrapper>, SupplierPresenter>(), SupplierContract.View {

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

            actionBar.title = "Sản phẩm"
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
        super.setupRecylcerView(false)
        val rv = getRecyclerView()
        rv.addOnScrollListener(object : EndlessRecyclerViewListener() {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                mPresenter.loadNextPage()
            }
        })
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = ProductBySupplierAdapter(context, recyclerView)
        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->

            if (obj is ProductWrapper) {
                val product = obj.getData()
                mPresenter.setProduct(product)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair.create(toolbar, "" + toolbar.transitionName),
                Pair.create(anchor, "" + anchor.transitionName),
                Pair.create(anchor, "" + anchor.transitionName)
            )
            Navigator.startBoxByProduct(this@SupplierActivity, position, options)

        })
        return adapter
    }

    override fun setData(data: List<ProductWrapper>) {
        val adapter = getAdapter<ProductBySupplierAdapter>()
        adapter.data = data
    }

    class ProductBySupplierAdapter(
        context: Context,
        recyclerView: RecyclerView
    ) : BaseAdapter<List<ProductWrapper>, ProductBySupplierViewHolder>(context, recyclerView) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductBySupplierViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_product, parent, false)
            return ProductBySupplierViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ProductBySupplierViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<ProductWrapper>(position)!!
            holder.bindData(data)
        }

        override fun getItemCount(): Int {
            if (mData == null) return 0
            return mData!!.size
        }
    }
}