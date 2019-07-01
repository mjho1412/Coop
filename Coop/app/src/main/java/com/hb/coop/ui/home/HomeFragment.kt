package com.hb.coop.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.ui.base.AutoFitGridLayoutManager
import com.hb.coop.utils.image.BitmapUtils
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRFragment
import com.hb.lib.utils.ui.ScreenUtils
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.hb.uiwidget.recyclerview.ListSpacingDecoration
import com.hb.uiwidget.recyclerview.OnItemClickListener
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter

class HomeFragment : HBMvpLceSRFragment<List<ActionObject>, HomePresenter>(), HomeContract.View {

    companion object {
        const val NUM_COLUMNS = 3
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLceViewHolder.contentView.isEnabled = false
    }

    override fun setupRecylcerView(addItemDecoration: Boolean) {
        super.setupRecylcerView(false)

        val adapter = getAdapter<ActionAdapter>()
        adapter.numColumns = NUM_COLUMNS
        val rv = getRecyclerView()
        val lm = AutoFitGridLayoutManager(context, ScreenUtils.getScreenWidth(context) / NUM_COLUMNS)
        rv.layoutManager = lm
        rv.addItemDecoration(ListSpacingDecoration(ThemeUtils.dpToPx(context!!, 2)))
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = ActionAdapter(context, recyclerView)
        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->
            if (obj is ActionObject) {
                obj.listener?.onClick()
            }
        })
        return adapter
    }

    override fun getResLayoutId(): Int {
        return R.layout.view_lce_swiperefresh
    }

    override fun setData(data: List<ActionObject>) {
        val adapter = getAdapter<ActionAdapter>()
        adapter.data = data
    }

    internal class ActionViewHolder(itemView: View) : BaseViewHolder<ActionObject>(itemView) {
        @BindView(R.id.text_view_title)
        lateinit var title: TextView
        @BindView(R.id.image_view_icon)
        lateinit var icon: ImageView

        init {
            val context = itemView.context
            val wScreen = ScreenUtils.getScreenWidth(context)
            val paddingTotal = ThemeUtils.dpToPx(context, 32)
            var sizeIcon = (wScreen / NUM_COLUMNS) - paddingTotal
            sizeIcon = sizeIcon * 50 / 100

            val lps = icon.layoutParams
            lps.width = sizeIcon
            lps.height = sizeIcon
            icon.layoutParams = lps
        }

        override fun bindData(data: ActionObject) {
            if (data.isEnabled) {
                icon.setImageResource(data.icon)
            } else {
                BitmapUtils.loadImageVectorResId(icon, data.icon, R.color.grey)
            }
            title.text = data.title
            val fontSize = when(NUM_COLUMNS) {
                2 -> 18
                3 -> 16
                else -> 14
            }
            title.textSize = fontSize.toFloat()

            title.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            if (!data.isEnabled) {
                title.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
            }
        }
    }

    internal class HeaderActionViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {
        @BindView(android.R.id.text1)
        lateinit var title: TextView

        override fun bindData(data: String) {
            title.text = data
        }
    }

    internal class ActionAdapter(
        context: Context, rv: RecyclerView
    ) : BaseAdapter<List<ActionObject>, ActionViewHolder>(context, rv)
        ,StickyRecyclerHeadersAdapter<HeaderActionViewHolder>
    {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_action_object, parent, false)
            return ActionViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {

            val data = getItem<ActionObject>(position)!!
            holder.bindData(data)

            if (data.isShowed) {
                if (data.isEnabled) {
                    super.onBindViewHolder(holder, position)
                }
                holder.itemView.visibility = View.VISIBLE
            } else {
                holder.itemView.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            if (mData == null)
                return 0
            return mData!!.size
        }

        override fun getHeaderId(position: Int): Long {
            val data = getItem<ActionObject>(position)!!
            return data.group
        }

        override fun onCreateHeaderViewHolder(parent: ViewGroup?): HeaderActionViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_header_action_object, parent, false)
            return HeaderActionViewHolder(itemView)
        }

        override fun onBindHeaderViewHolder(holder: HeaderActionViewHolder, position: Int) {
            val data = getItem<ActionObject>(position)!!
            holder.bindData(data.groupTitle)
        }

        private var numColumns = 1

        fun setNumColumns(num: Int) {
            numColumns = num
        }

        override fun getNumColumns(): Int {
            return numColumns
        }
    }
}