package com.hb.coop.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.hb.coop.R
import com.hb.coop.data.DataManager
import com.hb.coop.navigation.Navigator
import com.hb.coop.ui.base.CountDrawable
import com.hb.coop.ui.home.HomeFragment
import com.hb.coop.ui.map.MapFragment
import com.hb.lib.data.IDataManager
import com.hb.lib.utils.RxBus
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity()
    , NavigationView.OnNavigationItemSelectedListener {

    companion object {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("vi"))!!
        val formatterShow = DateTimeFormatter.ofPattern("dd LLLL, yyyy", Locale.forLanguageTag("vi"))!!
    }


    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView

    @BindView(R.id.nav_view_notification)
    lateinit var navNotificationView: NavigationView

    private lateinit var mNavHeaderViewHolder: NavHeaderViewHolder

    @BindView(R.id.text_day_label)
    lateinit var mDayLabel: TextView

    @BindView(R.id.calendar_view_by_week)
    lateinit var mCalendarByWeekView: MaterialCalendarView

    @BindView(R.id.calendar_view_by_month)
    lateinit var mCalendarByMonthView: MaterialCalendarView

    @BindView(R.id.view_calendar_container)
    lateinit var mCalendarContainerView: View

    @BindView(R.id.bottom_navigation_view)
    lateinit var mBottomNavigationView: BottomNavigationView

    private var mFragments = arrayOf(
        HomeFragment(),
        Fragment(),
        MapFragment()
    )

    private var mHeightCalendarContainerView = 0
    private var mCalendarContainerViewVisible = true
    private var mCurrentFragment: Fragment = mFragments[0]

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var rxBus: RxBus

    @Inject
    lateinit var dataManager: IDataManager

    private fun busEvent(event: Any) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        disposable.clear()
        disposable.addAll(
            rxBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::busEvent)
        )

        setupToolbarAndNavigation()
        setupBottomNavigation()
        setupCalendar()
        setupAllScreen()

//        setLightNavBarCompat()
    }

    private fun setupAllScreen() {
        mFragments.forEach { fragment ->
            supportFragmentManager.beginTransaction()
                .apply {
                    add(R.id.view_content, fragment)
                    hide(fragment)
                }
                .commit()
        }

        supportFragmentManager.beginTransaction()
            .show(mCurrentFragment)
            .commit()
    }

    private fun setupToolbarAndNavigation() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent))

        navView.setNavigationItemSelectedListener(this)
        val count = navView.headerCount
        if (count > 0) {
            val headerView = navView.getHeaderView(0)
            mNavHeaderViewHolder = NavHeaderViewHolder(headerView)
            mNavHeaderViewHolder.bindData(data = dataManager().getPassport()!!)
        }


    }

    private fun dataManager() : DataManager {
        return dataManager as DataManager
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                (dataManager as DataManager).logout()
                Navigator.startPassport(this)
                finish()
            }
        }
        return true
    }

    private val onDateChangedListener =
        OnDateSelectedListener { widget, date, selected ->
            if (selected) {
                mDayLabel.text = date.date.format(formatterShow)
            }

            if (widget.id == R.id.calendar_view_by_month) {
                mCalendarByWeekView.selectedDate = date
                mCalendarByWeekView.currentDate = date
            } else {
                mCalendarByMonthView.selectedDate = date
                mCalendarByMonthView.currentDate = date
            }
        }

    private fun setupCalendar() {

        mCalendarByWeekView.setOnDateChangedListener(onDateChangedListener)
        mCalendarByMonthView.setOnDateChangedListener(onDateChangedListener)

        mCalendarByWeekView.setTitleFormatter { day -> formatter.format(day.date) }
        mCalendarByMonthView.setTitleFormatter { day -> formatter.format(day.date) }


        val today = CalendarDay.today()
        mDayLabel.text = today.date.format(formatterShow)


        mCalendarByWeekView.selectedDate = today
        mCalendarByWeekView.currentDate = today
        mCalendarByMonthView.selectedDate = today
        mCalendarByMonthView.currentDate = today

        mCalendarContainerView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mCalendarContainerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mHeightCalendarContainerView = mCalendarContainerView.height
                }
            }
        )


        mCalendarByWeekView.setLeftArrow(R.drawable.ic_chevron_left_white_24dp)
        mCalendarByWeekView.setRightArrow(R.drawable.ic_chevron_right_white_24dp)
        mCalendarByMonthView.setLeftArrow(R.drawable.ic_chevron_left_white_24dp)
        mCalendarByMonthView.setRightArrow(R.drawable.ic_chevron_right_white_24dp)

    }

    private fun setupBottomNavigation() {
        mBottomNavigationView.setOnNavigationItemSelectedListener {

            val pair = when (it.itemId) {
                R.id.action_home -> {
                    Pair(true, mFragments[0])
                }
                R.id.action_graph -> {
                    Pair(false, mFragments[1])
                }
                R.id.action_map -> {
                    Pair(false, mFragments[2])
                }
                else -> {
                    Pair(true, mFragments[0])
                }
            }

            val fragment = pair.second
            if (fragment != mCurrentFragment) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(mCurrentFragment)
                    .show(fragment)
                    .commitNow()
                mCurrentFragment = fragment
            }


            val calendarVisible = pair.first
            if (calendarVisible != mCalendarContainerViewVisible) {
                mCalendarContainerViewVisible = calendarVisible
                if (calendarVisible) {
                    val animator = ValueAnimator.ofFloat(0f, 1f)
                    animator.addUpdateListener { valueAnimator ->
                        val percent = valueAnimator.animatedValue as Float
                        val lps = mCalendarContainerView.layoutParams
                        lps.height = (percent * mHeightCalendarContainerView).toInt()
                        mCalendarContainerView.layoutParams = lps
                        mCalendarContainerView.alpha = percent
                    }
                    animator.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            val lps = mCalendarContainerView.layoutParams
                            lps.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            mCalendarContainerView.layoutParams = lps
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
                    animator.duration = 300
                    animator.start()

                } else {
                    val animator = ValueAnimator.ofFloat(1f, 0f)
                    animator.addUpdateListener { valueAnimator ->
                        val percent = valueAnimator.animatedValue as Float
                        val lps = mCalendarContainerView.layoutParams
                        lps.height = (percent * mHeightCalendarContainerView).toInt()
                        mCalendarContainerView.layoutParams = lps
                        mCalendarContainerView.alpha = percent
                    }
                    animator.duration = 300
                    animator.start()
                }
            }
            true
        }
    }


    override fun onBackPressed() {
        if (mCalendarByMonthView.visibility == View.VISIBLE) {
            mCalendarByMonthView.visibility = View.GONE
            mCalendarByWeekView.visibility = View.VISIBLE
            return
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn có muốn thoát ứng dụng")
                .setNegativeButton("Đồng ý") { dialog, which ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                .setPositiveButton("Không") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    @OnClick(R.id.text_day_label)
    fun onDayByMonth() {
        val vis = mCalendarByMonthView.visibility
        if (vis == View.VISIBLE) {
            mCalendarByMonthView.visibility = View.GONE
            mCalendarByWeekView.visibility = View.VISIBLE

            mDayLabel.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_expand_more_x,
                0,
                R.drawable.ic_expand_more_x,
                0
            )


        } else {
            mCalendarByMonthView.visibility = View.VISIBLE
            mCalendarByWeekView.visibility = View.GONE

            mDayLabel.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_expand_less_x,
                0,
                R.drawable.ic_expand_less_x,
                0
            )
        }
    }

    private lateinit var defaultMenu: Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        defaultMenu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        setCount(this, "$count")
        return true
    }

    private var count = 0

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_notification) {
            drawerLayout.closeDrawer(GravityCompat.START)
            drawerLayout.openDrawer(GravityCompat.END)
            return true
//        } else if (id == R.id.action_chat) {
//            if (count > 0)
//                count++
////            setCount(this, "$count")
//            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setCount(context: Context, count: String) {
        val menuItem = defaultMenu.findItem(R.id.action_notification)
        val icon = menuItem.icon as LayerDrawable

        val badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)
        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
    }

}
