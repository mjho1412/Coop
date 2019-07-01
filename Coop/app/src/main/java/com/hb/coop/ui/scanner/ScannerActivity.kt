package com.hb.coop.ui.scanner

import android.app.Activity
import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.hb.coop.BuildConfig
import com.hb.coop.R
import com.hb.coop.ui.base.SwipeToDeleteCallback
import com.hb.lib.mvp.impl.HBMvpActivity
import com.hb.uiwidget.recyclerview.BaseAdapter
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.hb.uiwidget.recyclerview.DividerItemDecoration
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : HBMvpActivity<ScannerPresenter>(),
    ScannerContract.View,
    ZXingScannerView.ResultHandler {

    override fun getResLayoutId(): Int {
        return R.layout.activity_scanner
    }

    private lateinit var mScannerView: ZXingScannerView
    private var mFlash: Boolean = false
    private var mAutoFocus: Boolean = true
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        val contentFrame = findViewById<ViewGroup>(R.id.content_frame)
        mScannerView = ZXingScannerView(this)
        setupFormats()
        contentFrame.addView(mScannerView)

        if (BuildConfig.DEBUG) {
            for (i in 0..20) {
                mPresenter.addContentScanner("${i + 1}")
            }
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.scanner, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
            R.id.action_clear_data -> {
                clearListScanned()
                return true
            }
            R.id.action_list_scanned -> {
                showListScanned()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera(mCameraId)
        mScannerView.flash = mFlash
        mScannerView.setAutoFocus(mAutoFocus)
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
        }

        showMessageDialog(
            """Contents = ${rawResult.text}, Format = ${rawResult.barcodeFormat}""",
            rawResult.text
        )
    }

    private fun showMessageDialog(message: String, content: String) {
        showErrorDialog(message, View.OnClickListener {
            mPresenter.addContentScanner(content)
            mScannerView.resumeCameraPreview(this)
        })
    }

    private fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()
        if (mSelectedIndices == null || mSelectedIndices?.isEmpty()!!) {
            mSelectedIndices = ArrayList()
            for (i in ZXingScannerView.ALL_FORMATS.indices) {
                mSelectedIndices?.add(i)
            }
        }

        for (index in mSelectedIndices!!) {
            formats.add(ZXingScannerView.ALL_FORMATS[index])
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats)
        }
    }

    private fun clearListScanned() {
        AlertDialog.Builder(this)
            .setMessage("Bạn có muốn xóa hết danh sách đã quét")
            .setPositiveButton("Đồng ý") { dialog, which -> mPresenter.clearAllContentScanner() }
            .setNegativeButton("Không") { dialog, which -> }
            .create().show()
    }

    private fun showListScanned() {
        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_recycler, null, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_list_items)

        val data = mPresenter.getAllContentScanner()
        val adapter = ScannerAdapter(this, recyclerView)
        adapter.data = data
        recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@ScannerActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    this@ScannerActivity,
                    RecyclerView.VERTICAL,
                    R.drawable.divider_dark_gray
                )
            )
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as ScannerAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        sheet.setContentView(view)
        sheet.show()
    }

    @OnClick(R.id.fab_completed)
    fun onCompleted() {
        mPresenter.saveBarcodeList()
    }

    override fun completed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(error: Throwable) {
        val message = error.message!!
        showErrorDialog(message, View.OnClickListener {
        })
    }

    class ScannerViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {

        @BindView(android.R.id.text1)
        lateinit var title: TextView

        override fun bindData(data: String) {
            title.text = data
        }
    }


    class ScannerAdapter(context: Context, rv: RecyclerView) :
        BaseAdapter<ArrayList<String>, ScannerViewHolder>(context, rv) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannerViewHolder {
            val itemView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ScannerViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ScannerViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val data = getItem<String>(position)!!
            holder.bindData(data)
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
    }

}