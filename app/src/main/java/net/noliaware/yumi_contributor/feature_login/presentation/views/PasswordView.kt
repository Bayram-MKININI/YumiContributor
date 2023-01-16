package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isEmpty
import com.google.android.material.textview.MaterialTextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.*
import kotlin.math.roundToInt

class PasswordView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var inputCodeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var codeLinearLayout: LinearLayoutCompat
    private lateinit var separatorLineView: View

    private lateinit var padFirstDigit: TextView
    private lateinit var padSecondDigit: TextView
    private lateinit var padThirdDigit: TextView
    private lateinit var padFourthDigit: TextView
    private lateinit var padFifthDigit: TextView
    private lateinit var padSixthDigit: TextView
    private lateinit var padSeventhDigit: TextView
    private lateinit var padEighthDigit: TextView
    private lateinit var padNinthDigit: TextView
    private lateinit var padTenthDigit: TextView

    private lateinit var deleteImageView: ImageView
    private lateinit var confirmTextView: TextView

    var callback: PasswordViewCallback? by weak()

    interface PasswordViewCallback {
        fun onPadClickedAtIndex(index: Int)
        fun onDeleteButtonPressed()
        fun onConfirmButtonPressed()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        inputCodeTextView = findViewById(R.id.input_code_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        codeLinearLayout = findViewById(R.id.code_linear_layout)
        separatorLineView = findViewById(R.id.separator_line_view)

        padFirstDigit = findViewById(R.id.pad_first_digit)
        padFirstDigit.setOnClickListener(onPadButtonClickListener)

        padSecondDigit = findViewById(R.id.pad_second_digit)
        padSecondDigit.setOnClickListener(onPadButtonClickListener)

        padThirdDigit = findViewById(R.id.pad_third_digit)
        padThirdDigit.setOnClickListener(onPadButtonClickListener)

        padFourthDigit = findViewById(R.id.pad_fourth_digit)
        padFourthDigit.setOnClickListener(onPadButtonClickListener)

        padFifthDigit = findViewById(R.id.pad_fifth_digit)
        padFifthDigit.setOnClickListener(onPadButtonClickListener)

        padSixthDigit = findViewById(R.id.pad_sixth_digit)
        padSixthDigit.setOnClickListener(onPadButtonClickListener)

        padSeventhDigit = findViewById(R.id.pad_seventh_digit)
        padSeventhDigit.setOnClickListener(onPadButtonClickListener)

        padEighthDigit = findViewById(R.id.pad_eighth_digit)
        padEighthDigit.setOnClickListener(onPadButtonClickListener)

        padNinthDigit = findViewById(R.id.pad_ninth_digit)
        padNinthDigit.setOnClickListener(onPadButtonClickListener)

        padTenthDigit = findViewById(R.id.pad_tenth_digit)
        padTenthDigit.setOnClickListener(onPadButtonClickListener)

        deleteImageView = findViewById(R.id.delete_image_view)
        deleteImageView.setOnClickListener(onActionButtonClickListener)

        confirmTextView = findViewById(R.id.confirm_text_view)
        confirmTextView.setOnClickListener(onActionButtonClickListener)
    }

    private val onPadButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.pad_first_digit -> callback?.onPadClickedAtIndex(0)
                R.id.pad_second_digit -> callback?.onPadClickedAtIndex(1)
                R.id.pad_third_digit -> callback?.onPadClickedAtIndex(2)
                R.id.pad_fourth_digit -> callback?.onPadClickedAtIndex(3)
                R.id.pad_fifth_digit -> callback?.onPadClickedAtIndex(4)
                R.id.pad_sixth_digit -> callback?.onPadClickedAtIndex(5)
                R.id.pad_seventh_digit -> callback?.onPadClickedAtIndex(6)
                R.id.pad_eighth_digit -> callback?.onPadClickedAtIndex(7)
                R.id.pad_ninth_digit -> callback?.onPadClickedAtIndex(8)
                R.id.pad_tenth_digit -> callback?.onPadClickedAtIndex(9)
            }
        }
    }

    private val onActionButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.delete_image_view -> callback?.onDeleteButtonPressed()
                R.id.confirm_text_view -> callback?.onConfirmButtonPressed()
            }
        }
    }

    fun fillPadViewWithData(padDigits: List<Int>) {
        padDigits.forEachIndexed { index, digit ->
            resolveDigitIconAtIndexForValue(index, digit.toString())
        }
    }

    private fun resolveDigitIconAtIndexForValue(index: Int, value: String) {
        when (index) {
            0 -> padFirstDigit.text = value
            1 -> padSecondDigit.text = value
            2 -> padThirdDigit.text = value
            3 -> padFourthDigit.text = value
            4 -> padFifthDigit.text = value
            5 -> padSixthDigit.text = value
            6 -> padSeventhDigit.text = value
            7 -> padEighthDigit.text = value
            8 -> padNinthDigit.text = value
            9 -> padTenthDigit.text = value
        }
    }

    fun addSecretDigit() {

        val star = MaterialTextView(context).also {
            it.text = "*"
            it.typeface = ResourcesCompat.getFont(context, R.font.sf_pro_text_semibold)
            it.textSize = 35f
            it.setTextColor(ContextCompat.getColor(context, R.color.black_font))
        }

        codeLinearLayout.addView(star)
    }

    fun removeOneSecretDigit() {
        if (!codeLinearLayout.isEmpty())
            codeLinearLayout.removeViewAt(codeLinearLayout.childCount - 1)
    }

    fun clearSecretDigits() {
        codeLinearLayout.removeAllViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        inputCodeTextView.measureWrapContent()

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        codeLinearLayout.measure(
            MeasureSpec.makeMeasureSpec(
                (viewWidth / GOLDEN_RATIO).roundToInt(),
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        separatorLineView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(1), MeasureSpec.EXACTLY)
        )

        measurePadView(padFirstDigit)
        measurePadView(padSecondDigit)
        measurePadView(padThirdDigit)
        measurePadView(padFourthDigit)
        measurePadView(padFifthDigit)
        measurePadView(padSixthDigit)
        measurePadView(padSeventhDigit)
        measurePadView(padEighthDigit)
        measurePadView(padNinthDigit)
        measurePadView(padTenthDigit)

        deleteImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(47), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(35), MeasureSpec.EXACTLY)
        )

        confirmTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 7 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    private fun measurePadView(padView: View) {
        padView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        inputCodeTextView.layoutToTopLeft(
            (viewWidth - inputCodeTextView.measuredWidth) / 2,
            convertDpToPx(20)
        )

        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            inputCodeTextView.bottom + convertDpToPx(5)
        )

        codeLinearLayout.layoutToTopLeft(
            (viewWidth - codeLinearLayout.measuredWidth) / 2,
            descriptionTextView.bottom + convertDpToPx(30)
        )

        separatorLineView.layoutToTopLeft(
            (viewWidth - separatorLineView.measuredWidth) / 2,
            codeLinearLayout.bottom + convertDpToPx(10)
        )

        val spaceBetweenPads = padFirstDigit.measuredWidth / 3
        val contentHeight = padFirstDigit.measuredHeight * 7 / 3
        val emptySpace = viewHeight - (convertDpToPx(80)) - separatorLineView.bottom

        padFirstDigit.layoutToTopLeft(
            (viewWidth - padFirstDigit.measuredWidth * 19 / 3) / 2,
            separatorLineView.bottom + (emptySpace * (1 - 1 / GOLDEN_RATIO)).toInt() - contentHeight / 2
        )

        padSecondDigit.layoutToTopLeft(
            padFirstDigit.right + spaceBetweenPads,
            padFirstDigit.top
        )

        padThirdDigit.layoutToTopLeft(
            padSecondDigit.right + spaceBetweenPads,
            padFirstDigit.top
        )

        padFourthDigit.layoutToTopLeft(
            padThirdDigit.right + spaceBetweenPads,
            padFirstDigit.top
        )

        padFifthDigit.layoutToTopLeft(
            padFourthDigit.right + spaceBetweenPads,
            padFirstDigit.top
        )

        padSixthDigit.layoutToTopLeft(
            padFirstDigit.left,
            padFirstDigit.bottom + spaceBetweenPads
        )

        padSeventhDigit.layoutToTopLeft(
            padSixthDigit.right + spaceBetweenPads,
            padSixthDigit.top
        )

        padEighthDigit.layoutToTopLeft(
            padSeventhDigit.right + spaceBetweenPads,
            padSixthDigit.top
        )

        padNinthDigit.layoutToTopLeft(
            padEighthDigit.right + spaceBetweenPads,
            padSixthDigit.top
        )

        padTenthDigit.layoutToTopLeft(
            padNinthDigit.right + spaceBetweenPads,
            padSixthDigit.top
        )

        deleteImageView.layoutToTopRight(
            padTenthDigit.right,
            padTenthDigit.bottom + spaceBetweenPads
        )

        confirmTextView.layoutToBottomLeft(
            (viewWidth - confirmTextView.measuredWidth) / 2,
            bottom - convertDpToPx(40)
        )
    }
}