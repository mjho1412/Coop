package com.hb.coop.ui.test.order

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.*
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.ui.base.AppWithTranslucentStatusMvpFragment
import com.hb.coop.ui.base.listener.BottomNavigationViewListener
import com.hb.coop.ui.test.order.detail.OrderDetailsFragment
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.OnItemClickListener
import timber.log.Timber

class OrderFragment : AppWithTranslucentStatusMvpFragment<OrderPresenter>(), OrderContract.View, OnItemClickListener {

    companion object {
        fun newInstance(): OrderFragment {
            return OrderFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is BottomNavigationViewListener) {
            bottomNavListener = activity as BottomNavigationViewListener
        } else {
            throw ClassCastException("$activity must implement BottomNavigationViewListener")
        }
    }

    private var bottomNavListener: BottomNavigationViewListener? = null


    override fun getResLayoutId(): Int {
        return R.layout.fragment_order
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: CardView

    @BindView(R.id.details_toolbar_transition_helper)
    lateinit var transitionHelper: View

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.root)
    lateinit var root: ViewGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        if (savedInstanceState == null) {
            root.doOnLayout {
                toolbar.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(400)
                    .start()
                bottomNavListener?.showBottomNavigationView()
            }
        } else {
            toolbar.alpha = 1f
            toolbar.translationY = 0f
        }

//        mPresenter.loadData()
    }


    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    private fun setupViews() {
        supportsLollipop {
            transitionHelper.transitionName = TRANSITION_TOOLBAR
        }

        transitionHelper.translationY = -resources.getDimension(R.dimen.details_toolbar_container_height)
        toolbar.translationY = -toolbar.context.getToolbarHeight().toFloat()

        val elevation = resources.getDimension(R.dimen.toolbar_elevation)

        with(recyclerView) {
            val ad = OrderAdapter(context!!, recyclerView).apply {
                setOnItemClickListener(this@OrderFragment)
            }
            ad.data = DataProvider.getData()
            adapter = ad
            setHasFixedSize(true)
            val lm = layoutManager as LinearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) bottomNavListener?.hideBottomNavigationView()
                    if (dy < 0) bottomNavListener?.showBottomNavigationView()

                    if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (toolbar.cardElevation == 0f) return
                        animateToolbarElevation(true)

                    } else {
                        if (toolbar.cardElevation > 0f) return
                        toolbar.cardElevation = elevation
                    }
                }
            })

        }
    }

    override fun showLoading() {

    }

    override fun showError(error: Throwable) {
    }


    override fun setData(data: List<DataWrapper<String>>) {
    }

    override fun showContent() {
    }

    override fun onItemClick(view: View, obj: Any, position: Int) {
        val fragmentTransaction = initFragmentTransaction(view)
        val copy = view.copyViewImage()
        copy.y += toolbar.height
        root.addView(copy)
        view.visibility = View.INVISIBLE
        startAnimation(copy, fragmentTransaction)

    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + toolbar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = OrderDetailsFragment.newInstance(positions, adapterPosition)

        val transaction = fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, detailsFragment, OrderDetailsFragment.TAG)
            ?.addToBackStack(null)

        supportsLollipop {
            val transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)

            Timber.d(view.transitionName)

            detailsFragment.sharedElementEnterTransition = transition
            transaction
                ?.addSharedElement(view, view.transitionName)
                ?.addSharedElement(transitionHelper, transitionHelper.transitionName)
        }

        return transaction

    }

    private fun startAnimation(view: View, fragamentTransaction: FragmentTransaction?) {
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(recyclerView)
            withStartAction {
                if (toolbar.cardElevation > 0) animateToolbarElevation(true)
            }
            withEndAction {
                recyclerView.visibility = View.INVISIBLE

                val toY = resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                view.animate().y(toY).start()

                toolbar.animate()
                    .translationY(-toolbar.height.toFloat())
                    .alpha(0f)
                    .setDuration(600)
                    .withStartAction {
                        bottomNavListener?.hideBottomNavigationView()
                        transitionHelper.animate().translationY(0f).setDuration(500).start()
                    }
                    .withEndAction {
                        fragamentTransaction?.commitAllowingStateLoss()
                    }
                    .start()
            }
            start()
        }
    }

    private fun animateToolbarElevation(animateOut: Boolean) {
        var valueFrom = resources.getDimension(R.dimen.toolbar_elevation)
        var valueTo = 0f
        if (!animateOut) {
            valueTo = valueFrom
            valueFrom = 0f
        }

        ValueAnimator.ofFloat(valueFrom, valueTo).setDuration(250).apply {
            startDelay = 0
            addUpdateListener {
                toolbar.cardElevation = it.animatedValue as Float
            }
            start()
        }
    }


    internal class OrderAdapter(context: Context, rv: RecyclerView) :
        BaseAdapter<List<DataWrapper<String>>, OrderViewHolder>(context, rv) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_order, parent, false)
            return OrderViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<DataWrapper<String>>(position)!!
            holder.bindData(data)
        }

        override fun getItemCount(): Int {
            if (mData == null) return 0
            return mData!!.size
        }
    }

}