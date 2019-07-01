package com.hb.coop.ui.test.order.detail

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import butterknife.BindView
import com.hb.coop.R
import com.hb.coop.common.*
import com.hb.coop.ui.base.AppWithTranslucentStatusMvpFragment
import com.hb.coop.ui.base.listener.OnBackPressedListener
import com.hb.coop.ui.test.order.detail.OrderDetailsContract.Companion.EXTRA_COORDINATES
import com.hb.coop.ui.test.order.detail.OrderDetailsContract.Companion.EXTRA_POSITION
import kotlinx.android.synthetic.main.fragment_order_detail.*
import java.util.*

class OrderDetailsFragment : AppWithTranslucentStatusMvpFragment<OrderDetailsPresenter>(), OrderDetailsContract.View,
    OnBackPressedListener {


    companion object {
        const val TAG = "OrderDetailsFragment"

        fun newInstance(coordinates: FloatArray, adapterPosition: Int): OrderDetailsFragment {
            val params = Bundle().apply {
                putFloatArray(OrderDetailsContract.EXTRA_COORDINATES, coordinates)
                putInt(OrderDetailsContract.EXTRA_POSITION, adapterPosition)
            }
            return OrderDetailsFragment().apply {
                arguments = params
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            coordinates = it.getFloatArray(EXTRA_COORDINATES)
        }
    }

    private lateinit var coordinates: FloatArray

    override fun getResLayoutId(): Int {
        return R.layout.fragment_order_detail
    }

    @BindView(R.id.tv_title)
    lateinit var titleView: TextView
    @BindView(R.id.tv_title_date)
    lateinit var dateView: TextView
    @BindView(R.id.tv_description)
    lateinit var descriptionView: TextView

    @BindView(R.id.details_card)
    lateinit var detailsCard: View
    @BindView(R.id.toolbar_container)
    lateinit var toolbarContainer: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = if (arguments != null) (arguments as Bundle).getInt(EXTRA_POSITION)
        else 0

        setupViews(position)

        if (savedInstanceState == null) {
            animateToolbar()
            fab_negative.animate()
                .translationY(0f)
                .setDuration(650)
                .setInterpolator(OvershootInterpolator(4f))
                .start()
            fab_positive.animate()
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(650)
                .setInterpolator(OvershootInterpolator(4f))
                .start()
        } else {
            toolbar.alpha = 1f
        }
    }

    private fun setupViews(position: Int) {
        supportsLollipop {
            detailsCard.transitionName = TRANSITION_CARD + position
            toolbarContainer.transitionName = TRANSITION_TOOLBAR

        }

        (detailsCard.layoutParams as ViewGroup.MarginLayoutParams).topMargin = coordinates[2].toInt()

        val data = DataProvider.getData()[position]
        titleView.text = data.getTitle()
        dateView.text = AppConstants.formatDate.format(Date())
        descriptionView.text = data.getDescription()

        fab_negative.setOnClickListener { onBackPressed() }
        fab_positive.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        animateViewsOut()
    }

    private fun animateViewsOut() {
        val translateTo = fab_negative.height * 2f
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(recycler_view)
            start()
        }

        fab_negative.animate()
            .translationY(translateTo)
            .setDuration(350)
            .setInterpolator(AnticipateInterpolator(2f))
            .start()
        fab_positive.animate()
            .translationY(translateTo)
            .setStartDelay(50)
            .setDuration(350)
            .withEndAction {
                activity?.supportFragmentManager?.popBackStack()
            }
            .setInterpolator(AnticipateInterpolator(2f))
            .start()

        animateToolbar(0f, 350)
    }

    private fun animateToolbar(alphaTo: Float = 1f, duration: Long = 200) {
        toolbar.animate().alpha(alphaTo).setDuration(duration).start()
    }


}