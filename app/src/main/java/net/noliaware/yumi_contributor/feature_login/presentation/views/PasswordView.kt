package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class PasswordView(context: Context, attrs: AttributeSet?) : ElevatedCardView(context, attrs) {

    private lateinit var inputCodeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var codeTextView: TextView

    private lateinit var padFirstDigit: View
    private lateinit var padFirstDigitTextView: TextView

    private lateinit var padSecondDigit: View
    private lateinit var padSecondDigitTextView: TextView

    private lateinit var padThirdDigit: View
    private lateinit var padThirdDigitTextView: TextView

    private lateinit var padFourthDigit: View
    private lateinit var padFourthDigitTextView: TextView

    private lateinit var padFifthDigit: View
    private lateinit var padFifthDigitTextView: TextView

    private lateinit var padSixthDigit: View
    private lateinit var padSixthDigitTextView: TextView

    private lateinit var padSeventhDigit: View
    private lateinit var padSeventhDigitTextView: TextView

    private lateinit var padEighthDigit: View
    private lateinit var padEighthDigitTextView: TextView

    private lateinit var padNinthDigit: View
    private lateinit var padNinthDigitTextView: TextView

    private lateinit var padTenthDigit: View
    private lateinit var padTenthDigitTextView: TextView

    private lateinit var deleteTextView: TextView
    private lateinit var confirmImageView: ImageView
    private lateinit var confirmTextView: TextView

    var callback: PasswordViewCallback? by weak()

    interface PasswordViewCallback {
        fun onPadClickedAtIndex(index: Int)
        fun onClearButtonPressed()
        fun onConfirmButtonPressed()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        inputCodeTextView = findViewById(R.id.input_code_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        codeTextView = findViewById(R.id.code_text_view)

        padFirstDigit = findViewById(R.id.pad_first_digit)
        padFirstDigit.setOnClickListener(onPadButtonClickListener)
        padFirstDigitTextView = padFirstDigit.findViewById(R.id.pad_first_digit_text_view)

        padSecondDigit = findViewById(R.id.pad_second_digit)
        padSecondDigit.setOnClickListener(onPadButtonClickListener)
        padSecondDigitTextView = padSecondDigit.findViewById(R.id.pad_second_digit_text_view)

        padThirdDigit = findViewById(R.id.pad_third_digit)
        padThirdDigit.setOnClickListener(onPadButtonClickListener)
        padThirdDigitTextView = padThirdDigit.findViewById(R.id.pad_third_digit_text_view)

        padFourthDigit = findViewById(R.id.pad_fourth_digit)
        padFourthDigit.setOnClickListener(onPadButtonClickListener)
        padFourthDigitTextView = padFourthDigit.findViewById(R.id.pad_fourth_digit_text_view)

        padFifthDigit = findViewById(R.id.pad_fifth_digit)
        padFifthDigit.setOnClickListener(onPadButtonClickListener)
        padFifthDigitTextView = padFifthDigit.findViewById(R.id.pad_fifth_digit_text_view)

        padSixthDigit = findViewById(R.id.pad_sixth_digit)
        padSixthDigit.setOnClickListener(onPadButtonClickListener)
        padSixthDigitTextView = padSixthDigit.findViewById(R.id.pad_sixth_digit_text_view)

        padSeventhDigit = findViewById(R.id.pad_seventh_digit)
        padSeventhDigit.setOnClickListener(onPadButtonClickListener)
        padSeventhDigitTextView = padSeventhDigit.findViewById(R.id.pad_seventh_digit_text_view)

        padEighthDigit = findViewById(R.id.pad_eighth_digit)
        padEighthDigit.setOnClickListener(onPadButtonClickListener)
        padEighthDigitTextView = padEighthDigit.findViewById(R.id.pad_eighth_digit_text_view)

        padNinthDigit = findViewById(R.id.pad_ninth_digit)
        padNinthDigit.setOnClickListener(onPadButtonClickListener)
        padNinthDigitTextView = padNinthDigit.findViewById(R.id.pad_ninth_digit_text_view)

        padTenthDigit = findViewById(R.id.pad_tenth_digit)
        padTenthDigit.setOnClickListener(onPadButtonClickListener)
        padTenthDigitTextView = padTenthDigit.findViewById(R.id.pad_tenth_digit_text_view)

        deleteTextView = findViewById(R.id.delete_text_view)
        deleteTextView.setOnClickListener(onActionButtonClickListener)

        confirmImageView = findViewById(R.id.confirm_pass_image_view)
        confirmImageView.setOnClickListener(onActionButtonClickListener)
        confirmTextView = findViewById(R.id.confirm_pass_text_view)
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
                R.id.delete_text_view -> callback?.onClearButtonPressed()
                R.id.confirm_pass_image_view -> callback?.onConfirmButtonPressed()
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
            0 -> padFirstDigitTextView.text = value
            1 -> padSecondDigitTextView.text = value
            2 -> padThirdDigitTextView.text = value
            3 -> padFourthDigitTextView.text = value
            4 -> padFifthDigitTextView.text = value
            5 -> padSixthDigitTextView.text = value
            6 -> padSeventhDigitTextView.text = value
            7 -> padEighthDigitTextView.text = value
            8 -> padNinthDigitTextView.text = value
            9 -> padTenthDigitTextView.text = value
        }
    }

    fun addSecretDigit() {
        codeTextView.text = StringBuilder().apply {
            append(codeTextView.text.toString())
            if (codeTextView.length() > 0)
                append(" ")
            append("*")
        }.toString()
    }

    fun clearSecretDigits() {
        if (codeTextView.length() > 0)
            codeTextView.text = ""
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        val rectangleWidth = viewWidth * 9 / 10

        inputCodeTextView.measureWrapContent()

        descriptionTextView.measureWrapContent()

        codeTextView.measure(
            MeasureSpec.makeMeasureSpec(rectangleWidth * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(42), MeasureSpec.EXACTLY)
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

        deleteTextView.measureWrapContent()
        confirmImageView.measureWrapContent()
        confirmTextView.measureWrapContent()

        viewHeight = inputCodeTextView.measuredHeight + descriptionTextView.measuredHeight + codeTextView.measuredHeight +
                padFirstDigit.measuredHeight * 2 + confirmImageView.measuredHeight + convertDpToPx(150)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    private fun measurePadView(padView: View) {
        padView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(42), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(42), MeasureSpec.EXACTLY)
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

        codeTextView.layoutToTopLeft(
            (viewWidth - codeTextView.measuredWidth) / 2,
            descriptionTextView.bottom + convertDpToPx(10)
        )

        val spaceBetweenPads = convertDpToPx(10)

        padFirstDigit.layoutToTopLeft(
            (viewWidth - (padFirstDigit.measuredWidth * 5 + spaceBetweenPads * 4)) / 2,
            codeTextView.bottom + convertDpToPx(15)
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

        deleteTextView.layoutToTopLeft(
            (viewWidth - deleteTextView.measuredWidth) / 2,
            padTenthDigit.bottom + convertDpToPx(20)
        )

        confirmImageView.layoutToTopLeft(
            (viewWidth - confirmImageView.measuredWidth) / 2,
            deleteTextView.bottom + convertDpToPx(20)
        )

        confirmTextView.layoutToTopLeft(
            (viewWidth - confirmTextView.measuredWidth) / 2,
            confirmImageView.top + convertDpToPx(7)
        )
    }
}