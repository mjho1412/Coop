package com.hb.coop.ui.system.basket

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.hb.coop.R
import com.hb.coop.common.TRANSITION_TOOLBAR
import com.hb.coop.data.entity.Basket
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.ui.base.AutoFitGridLayoutManager
import com.hb.coop.utils.image.BitmapUtils
import com.hb.lib.mvp.impl.lce.sr.HBMvpLceSRActivity
import com.hb.lib.utils.Utils
import com.hb.lib.utils.ui.ScreenUtils
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.uiwidget.recyclerview.*
import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import com.vincentbrison.openlibraries.android.dualcache.DualCache
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class BasketActivity : HBMvpLceSRActivity<List<DataWrapper<*>>, BasketPresenter>(), BasketContract.View {

    override fun getResLayoutId(): Int {
        return R.layout.activity_lce_sr_search
    }

    companion object {
        const val NUM_COLUMNS = 2
        const val CACHE_NAME = "QR_CODE"
        const val VERSION = 1
        const val KB = 1024
        val MB = 1024 * KB
        val RAM_MAX_SIZE = 5 * MB
        val DISK_MAX_SIZE = 30 * MB
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.text_view_total)
    lateinit var totalView: TextView


    private lateinit var mCache: DualCache<Bitmap>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.transitionName = TRANSITION_TOOLBAR

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)

            actionBar.title = "Danh Sách Sọt Hàng"
        }

        totalView.visibility = View.GONE
        mLceViewHolder.contentView.isEnabled = false

        mCache = Builder(CACHE_NAME, VERSION, Bitmap::class.java)
            .enableLog()
            .useReferenceInRam(RAM_MAX_SIZE) { it.rowBytes * it.height }
            .useSerializerInDisk(DISK_MAX_SIZE, false, object : CacheSerializer<Bitmap> {
                override fun toString(bmp: Bitmap): String {
                    return BitmapUtils.bitmapToString(bmp)
                }

                override fun fromString(data: String): Bitmap {
                    return BitmapUtils.stringToBitMap(data)
                }

            }, this)
            .build()
    }

    override fun addBasketCompleted() {

    }

    override fun addBasketFailed() {

    }

    override fun setTotal(total: Int) {
        val text = "Số lượng: $total"
        totalView.text = text
        totalView.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basket_qrcode, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.action_add_basket) {
            mPresenter.addBasket()
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

        val adapter = getAdapter<BasketAdapter>()
        adapter.setNumColumns(NUM_COLUMNS)
        val lm = AutoFitGridLayoutManager(this, ScreenUtils.getScreenWidth(this) / NUM_COLUMNS)
        rv.layoutManager = lm
        rv.addItemDecoration(ListSpacingDecoration(ThemeUtils.dpToPx(this, 2)))
    }

    override fun createAdapter(context: Context, recyclerView: RecyclerView): RecyclerView.Adapter<*> {
        val adapter = BasketAdapter(context, recyclerView)
        adapter.setOnItemClickListener(OnItemClickListener { anchor, obj, position ->

        })
        return adapter
    }

    override fun setData(data: List<DataWrapper<*>>) {
        val adapter = getAdapter<BasketAdapter>()
        adapter.data = data
    }


    inner class BasketViewHolder(itemView: View) : BaseViewHolder<DataWrapper<*>>(itemView) {

        @BindView(R.id.image_view_basket_qrcode)
        lateinit var qrcode: ImageView

        override fun bindData(data: DataWrapper<*>) {
            val basket = data.getData() as Basket
            val color = if (basket.usedFlag) {
                R.color.fill_default
            } else {
                R.color.white
            }
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, color))

            val key = Utils.md5(basket.barcode)
            val bmp = mCache.get(key)
            if (bmp != null) {
                qrcode.setImageBitmap(bmp)
            } else {
                mPresenter.disposable.add(
                    Observable.create<Bitmap> {
                        val writer = QRCodeWriter()
                        try {
                            val bitMatrix = writer.encode(data.getTitle(), BarcodeFormat.QR_CODE, 512, 512)
                            val w = bitMatrix.width
                            val h = bitMatrix.height
                            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
                            for (x in 0..511) {
                                for (y in 0..511) {
                                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                                }
                            }
                            mCache.put(key, bmp)
                            it.onNext(bmp)
                            it.onComplete()
                        } catch (e: Exception) {
                            it.onError(e)
                        }
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            qrcode.setImageBitmap(it)
                        }, {
                            qrcode.setImageResource(R.drawable.no_image)
                            Timber.e(it)
                        })
                )
            }
        }
    }

    inner class BasketAdapter(
        context: Context,
        recyclerView: RecyclerView
    ) : BaseAdapter<List<DataWrapper<*>>, BasketViewHolder>(context, recyclerView) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
            val itemView = mInflater.inflate(R.layout.item_view_basket_qrcode, parent, false)
            return BasketViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<DataWrapper<*>>(position)!!
            holder.bindData(data)
        }

        override fun getItemCount(): Int {
            if (mData == null)
                return 0
            return mData!!.size
        }

        private var numColumns = 1

        fun setNumColumns(num: Int) {
            numColumns = num
        }

        fun getNumColumns(): Int {
            return numColumns
        }
    }
}