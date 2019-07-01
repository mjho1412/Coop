package com.hb.coop.ui.supplier.basket

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.afollestad.materialcab.MaterialCab
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.hb.coop.R
import com.hb.coop.common.TRANSITION_CARD
import com.hb.coop.common.TRANSITION_TOOLBAR
import com.hb.coop.common.supportsLollipop
import com.hb.coop.common.withEndAction
import com.hb.coop.data.api.response.coop.BasketScannerResponse
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.navigation.Navigator
import com.hb.coop.ui.base.SwipeToDeleteCallback
import com.hb.coop.ui.supplier.ProductBySupplierViewHolder
import com.hb.coop.ui.supplier.ProductWrapper
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.hb.uiwidget.recyclerview.dragselect.DragSelectReceiver
import com.hb.uiwidget.recyclerview.dragselect.DragSelectTouchListener
import com.hb.uiwidget.recyclerview.dragselect.Mode

class BasketByProductActivity : HBMvpLceSRActivity<ArrayList<DataWrapper<*>>, BoxByProductPresenter>(),
    BasketByProductContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_box_by_product
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.appbar)
    lateinit var appBar: AppBarLayout
    @BindView(R.id.details_card)
    lateinit var detailsCard: View
    lateinit var detailsCardViewHolder: ProductBySupplierViewHolder

    @BindView(R.id.view_lce_container)
    lateinit var mContainerView: View

    private lateinit var touchListener: DragSelectTouchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsCardViewHolder = ProductBySupplierViewHolder(detailsCard)
        detailsCardViewHolder.bindData(
            data = ProductWrapper(mPresenter.getProduct())
        )

        setupToolbar()

        val position = if (intent.extras != null) {
            intent.extras!!.getInt(BasketByProductContract.EXTRA_POSITION)
        } else 0

        setupViews(position)

        if (savedInstanceState == null) {
            animateToolbar()
        } else {
            toolbar.alpha = 1f
        }

        mContainerView.setBackgroundColor(Color.WHITE)
        mLceViewHolder.contentView.isEnabled = false

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        MaterialCab.saveState(outState)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)

            actionBar.title = " "
        }
    }


    private fun setupViews(position: Int) {
        supportsLollipop {
            detailsCard.transitionName = TRANSITION_CARD + position
            appBar.transitionName = TRANSITION_TOOLBAR
        }

    }

    override fun deleteCompleted(position: Int) {
//        val adapter = getAdapter<BoxAdapter>()
//        adapter.removeAt(position)
    }

    override fun deleteFailed(position: Int, data: DataWrapper<*>) {
        val adapter = getAdapter<BoxAdapter>()
        adapter.insertAt(position, data)
    }

    override fun updateBasketCompleted(position: Int, data: DataWrapper<*>) {
        val adapter = getAdapter<BoxAdapter>()
        adapter.notifyItemChanged(position)
    }

    override fun setupRecylcerView(addItemDecoration: Boolean) {
        super.setupRecylcerView(addItemDecoration)
        val rv = getRecyclerView()
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = getAdapter<BoxAdapter>()
                val position = viewHolder.adapterPosition
                val data = adapter.getItem<DataWrapper<*>>(position)!!

                adapter.removeAt(viewHolder.adapterPosition)
                mPresenter.deleteBasket(position, data)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rv)

        rv.addOnItemTouchListener(touchListener)
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
//        val adapter = BoxAdapter(context, recyclerView)
//        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->
//            showBottomSheetDialog(true, position, obj as DataWrapper<*>)
//        })
        val adapter = BoxAdapter(context, recyclerView, object : BoxAdapter.Listener {
            override fun onClick(index: Int) {
                val adapter = getAdapter<BoxAdapter>()
                adapter.toggleSelected(index)
            }

            override fun onLongClick(index: Int) {
                val adapter = getAdapter<BoxAdapter>()
                if (adapter.isSelected(index)) {
                    touchListener.setIsActive(false, index)
                } else {
                    touchListener.setIsActive(true, index)
                }
            }

            override fun onSelectionChanged(count: Int) {
                if (count <= 0) {
                    MaterialCab.destroy()
                    toolbar.visibility = View.VISIBLE
                } else {
                    val adapter = getAdapter<BoxAdapter>()
                    MaterialCab.attach(this@BasketByProductActivity, R.id.cab_stub) {
                        menuRes = R.menu.cab
                        closeDrawableRes = R.drawable.ic_close_black_24dp
                        titleColor = Color.WHITE
                        title = getString(R.string.basket_title_selected, count)
                        toolbar.visibility = View.GONE

                        onSelection {
                            if (it.itemId == R.id.done) {
                                Toast.makeText(
                                    this@BasketByProductActivity,
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

    override fun setData(data: ArrayList<DataWrapper<*>>) {
        val adapter = getAdapter<BoxAdapter>()
        adapter.data = data
    }

    override fun setTotal(total: Int) {
    }

    override fun onBackPressed() {
        if (!MaterialCab.destroy()) {
            animateViewsOut()
//            super.onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.box_by_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_add_box -> {
                Navigator.startScanner(this, BasketByProductContract.REQUEST_CODE_SCANNER)
            }
            R.id.action_list_completed -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BasketByProductContract.REQUEST_CODE_SCANNER -> {
                if (resultCode == Activity.RESULT_OK) {
                    runOnUiThread {
                        showBottomSheetDialog()
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun showBottomSheetDialog(updated: Boolean, position: Int, data: DataWrapper<*>?) {
        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_input_quantity, null, false)

        val inputView = view.findViewById<TextInputLayout>(R.id.input_number)
        val okButton = view.findViewById<Button>(R.id.button_ok)
        val cancelButton = view.findViewById<Button>(R.id.button_cancel)

        if (updated) {
            val d = data?.getData()
            if (d is BasketScannerResponse) {
                inputView.editText?.setText("${d.basketParams?.quantity}")
            }
        }

        cancelButton.setOnClickListener {
            sheet.dismiss()
        }

        okButton.setOnClickListener {

            val number = inputView.editText?.text.toString().trim().toInt()

            if (!updated) {
                mPresenter.insertBasketList(number)
            } else {
                mPresenter.updateBasket(number, position, data!!)
            }
            sheet.dismiss()
        }

        sheet.setCancelable(false)
        sheet.setContentView(view)
        sheet.show()
    }

    private fun animateToolbar(alphaTo: Float = 1f, duration: Long = 200) {
        toolbar.animate().alpha(alphaTo).setDuration(duration).start()
    }

    private fun animateViewsOut() {
        animateToolbar(0f, 350)
        val rv = getRecyclerView()
        AnimatorInflater.loadAnimator(this, R.animator.main_list_animator).apply {
            setTarget(rv)
            start()
            withEndAction {
                supportFinishAfterTransition()
            }
        }
    }

    class BoxViewHolder(
        itemView: View,
        private val callback: BoxAdapter.Listener?
    ) : BaseViewHolder<DataWrapper<*>>(itemView)
        , View.OnClickListener, View.OnLongClickListener {

        init {
            this.itemView.setOnClickListener(this)
            this.itemView.setOnLongClickListener(this)
        }

        @BindView(android.R.id.text1)
        lateinit var title: TextView

        @BindView(android.R.id.text2)
        lateinit var description: TextView

        @BindView(R.id.foreground)
        lateinit var foreground: View

        override fun bindData(data: DataWrapper<*>) {
            val msg = "Sọt hàng: ${data.getTitle()}"
            title.text = msg
            description.text = data.getDescription()
        }

        override fun onClick(v: View?) {
            callback?.onClick(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callback?.onLongClick(adapterPosition)
            return true
        }
    }

    class BoxAdapter(
        context: Context,
        rv: RecyclerView,
        private val callback: BoxAdapter.Listener?
    ) : BaseAdapter<ArrayList<DataWrapper<*>>, BoxViewHolder>(context, rv)
        , DragSelectReceiver {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_basket, parent, false)
            return BoxViewHolder(itemView, callback)
        }

        override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
//            super.onBindViewHolder(holder, position)
            val data = getItem<DataWrapper<*>>(position)!!
            holder.bindData(data)


            val d: Drawable? = if (selectedIndices.contains(position)) {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.color_selected))
                holder.description.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                ColorDrawable(ContextCompat.getColor(context, R.color.foreground_selected))
            } else {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.description.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                null
            }

            holder.foreground.background = d
        }

        override fun getItemCount(): Int {
            if (mData == null)
                return 0
            return mData!!.size
        }

        fun removeAt(position: Int) {
            mData!!.removeAt(position)
            notifyItemRemoved(position)
        }

        fun insertAt(position: Int, value: DataWrapper<*>) {
            mData!!.add(position, value)
            notifyItemInserted(position)
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

        override fun isSelected(index: Int): Boolean = selectedIndices.contains(index)

        override fun isIndexSelectable(index: Int): Boolean = true


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