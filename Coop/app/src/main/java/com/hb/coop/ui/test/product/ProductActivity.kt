package com.hb.coop.ui.test.product

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.afollestad.materialcab.MaterialCab
import com.hb.coop.R
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.hb.uiwidget.recyclerview.dragselect.DragSelectReceiver
import com.hb.uiwidget.recyclerview.dragselect.DragSelectTouchListener
import com.hb.uiwidget.recyclerview.dragselect.Mode
import timber.log.Timber

class ProductActivity : HBMvpLceSRActivity<List<Any>, ProductPresenter>(), ProductContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_lce_sr_search
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.text_view_total)
    lateinit var totalView: TextView

    private lateinit var touchListener: DragSelectTouchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)

            actionBar.title = "Sản phẩm"
        }

        totalView.visibility = View.GONE

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        MaterialCab.saveState(outState)
    }

    override fun setupRecylcerView(addItemDecoration: Boolean) {
        super.setupRecylcerView(addItemDecoration)

        val rv = getRecyclerView()
        rv.addOnItemTouchListener(touchListener)

        mLceViewHolder.contentView.isEnabled = false
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = ProductAdapter(context, recyclerView, object : ProductAdapter.Listener {
            override fun onClick(index: Int) {
                val adapter = getAdapter<ProductAdapter>()
                adapter.toggleSelected(index)
            }

            override fun onLongClick(index: Int) {
                touchListener.setIsActive(true, index)
            }

            override fun onSelectionChanged(count: Int) {
                Timber.d("Count $count")
                if (count <= 0) {
                    MaterialCab.destroy()
                    toolbar.visibility = View.VISIBLE
                } else {
                    val adapter = getAdapter<ProductAdapter>()
                    MaterialCab.attach(this@ProductActivity, R.id.cab_stub) {
                        menuRes = R.menu.cab
                        closeDrawableRes = R.drawable.ic_close_black_24dp
                        titleColor = Color.WHITE
                        title = getString(R.string.cab_title_x, count)
                        toolbar.visibility = View.GONE

                        onSelection {
                            if (it.itemId == R.id.done) {
                                Toast.makeText(
                                    this@ProductActivity,
                                    "Selected count: $count",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                adapter.clearSelected()
                                true
                            } else {
                                false
                            }
                        }

                        onDestroy {
                            adapter.clearSelected()
                            true
                        }
                    }
                }
            }
        })
        touchListener = DragSelectTouchListener.create(this, adapter) {
            hotspotHeight = ThemeUtils.dpToPx(context, 56)
            hotspotOffsetTop = 0
            hotspotOffsetBottom = 0

            autoScrollListener = {

            }
            mode = Mode.RANGE
        }
        return adapter
    }

    override fun onBackPressed() {
        if (!MaterialCab.destroy()) {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.action_search) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setData(data: List<Any>) {

    }

    class ProductViewHolder(
        itemView: View,
        private val callback: ProductAdapter.Listener?
    ) : BaseViewHolder<Any>(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            this.itemView.setOnClickListener(this)
            this.itemView.setOnLongClickListener(this)
        }

        @BindView(android.R.id.text1)
        lateinit var text1: TextView
        @BindView(android.R.id.text2)
        lateinit var text2: TextView

        @BindView(R.id.foreground)
        lateinit var foreground: FrameLayout

        override fun bindData(data: Any) {
            text1.text = data.toString()
            text2.text = data.toString()
        }

        override fun onClick(v: View?) {
            callback?.onClick(adapterPosition)

        }

        override fun onLongClick(v: View?): Boolean {
            callback?.onLongClick(adapterPosition)
            return true
        }
    }

    class ProductAdapter(
        context: Context,
        recyclerView: RecyclerView,
        private val callback: Listener?
    ) : BaseAdapter<List<Any>, ProductActivity.ProductViewHolder>(context, recyclerView)
        , DragSelectReceiver {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_test_product, parent, false)
            return ProductViewHolder(itemView, callback)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//            super.onBindViewHolder(holder, position)
            holder.bindData("Position: $position")

            val d: Drawable? = if (selectedIndices.contains(position)) {
                holder.text1.setTextColor(ContextCompat.getColor(context, R.color.color_selected))
                holder.text2.setTextColor(ContextCompat.getColor(context, R.color.color_selected))
                ColorDrawable(ContextCompat.getColor(context, R.color.foreground_selected))
            } else {
                holder.text1.setTextColor(ContextCompat.getColor(context, R.color.color_normal))
                holder.text2.setTextColor(ContextCompat.getColor(context, R.color.color_normal))
                null
            }

            holder.foreground.background = d
        }


        override fun getItemCount(): Int {
            return 100
        }


        private val selectedIndices: MutableList<Int> = mutableListOf()

        interface Listener {
            fun onClick(index: Int)

            fun onLongClick(index: Int)

            fun onSelectionChanged(count: Int)
        }

        override fun setSelected(index: Int, selected: Boolean) {
            if (!selected) {
                selectedIndices.remove(index)
            } else if (!selectedIndices.contains(index)) {
                selectedIndices.add(index)
            }
            notifyItemChanged(index)
            callback?.onSelectionChanged(selectedIndices.size)
        }

        override fun isSelected(index: Int) = selectedIndices.contains(index)

        override fun isIndexSelectable(index: Int) = true


        fun toggleSelected(index: Int) {
            if (selectedIndices.contains(index)) {
                selectedIndices.remove(index)
            } else {
                selectedIndices.add(index)
            }
            notifyItemChanged(index)
            callback?.onSelectionChanged(selectedIndices.size)
        }

        fun clearSelected() {
            if (selectedIndices.isEmpty()) {
                return
            }
            selectedIndices.clear()
            notifyDataSetChanged()
            callback?.onSelectionChanged(0)
        }
    }
}
