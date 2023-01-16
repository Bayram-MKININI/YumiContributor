package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isNotEmpty
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.DataValueView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class ProfileView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var myDataTextView: TextView
    private lateinit var myDataLinearLayout: LinearLayoutCompat
    private lateinit var complementaryDataTextView: TextView
    private lateinit var complementaryDataLinearLayout: LinearLayoutCompat
    private lateinit var getCodeTextView: TextView
    var callback: ProfileViewCallback? by weak()

    data class ProfileViewAdapter(
        val myDataAdapters: MutableList<DataValueView.DataValueViewAdapter> = mutableListOf(),
        val complementaryDataAdapters: MutableList<DataValueView.DataValueViewAdapter> = mutableListOf(),
    )

    interface ProfileViewCallback {
        fun onGetCodeButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        myDataTextView = findViewById(R.id.my_data_text_view)
        myDataLinearLayout = findViewById(R.id.my_data_linear_layout)
        complementaryDataTextView = findViewById(R.id.complementary_data_text_view)
        complementaryDataLinearLayout = findViewById(R.id.complementary_data_linear_layout)
        getCodeTextView = findViewById(R.id.get_code_text_view)
        getCodeTextView.setOnClickListener { callback?.onGetCodeButtonClicked() }
    }

    fun fillViewWithData(profileViewAdapter: ProfileViewAdapter) {

        if (myDataLinearLayout.isNotEmpty()) {
            myDataLinearLayout.removeAllViews()
        }

        profileViewAdapter.myDataAdapters.forEach { profileDataViewAdapter ->
            DataValueView(context).also {
                it.fillViewWithData(profileDataViewAdapter)
                myDataLinearLayout.addView(it)
            }
        }

        if (complementaryDataLinearLayout.isNotEmpty()) {
            complementaryDataLinearLayout.removeAllViews()
        }

        profileViewAdapter.complementaryDataAdapters.forEach { profileDataViewAdapter ->
            DataValueView(context).also {
                it.fillViewWithData(profileDataViewAdapter)
                complementaryDataLinearLayout.addView(it)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        myDataTextView.measureWrapContent()

        myDataLinearLayout.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        complementaryDataTextView.measureWrapContent()

        complementaryDataLinearLayout.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        getCodeTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 7 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        viewHeight = myDataTextView.measuredHeight + myDataLinearLayout.measuredHeight + complementaryDataTextView.measuredHeight +
                    complementaryDataLinearLayout.measuredHeight + getCodeTextView.measuredHeight + convertDpToPx(80)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        myDataTextView.layoutToTopLeft(
            convertDpToPx(20),
            convertDpToPx(10)
        )

        myDataLinearLayout.layoutToTopLeft(
            convertDpToPx(20),
            myDataTextView.bottom + convertDpToPx(10)
        )

        complementaryDataTextView.layoutToTopLeft(
            convertDpToPx(20),
            myDataLinearLayout.bottom + convertDpToPx(10)
        )

        complementaryDataLinearLayout.layoutToTopLeft(
            convertDpToPx(20),
            complementaryDataTextView.bottom + convertDpToPx(10)
        )

        getCodeTextView.layoutToTopLeft(
            (viewWidth - getCodeTextView.measuredWidth) / 2,
            complementaryDataLinearLayout.bottom + convertDpToPx(10)
        )
    }
}