package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.viewScope
import java.time.Duration

class MailRecipientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    private lateinit var contentView: View
    private lateinit var chipGroup: ChipGroup
    private lateinit var recipientEditText: EditText
    var mailDomain: String = ""
    private var debounceJob: Job? = null

    init {
        initView()
    }

    private fun initView() {
        inflate(
            layoutRes = R.layout.mail_recipient_layout,
            attachToRoot = true
        )
        contentView = findViewById(R.id.content_view)
        chipGroup = contentView.findViewById(R.id.recipients_chip_group)
        recipientEditText = contentView.findViewById(R.id.recipient_edit_text)
        recipientEditText.apply {
            var isDeleting = false
            doBeforeTextChanged { _, _, before, count ->
                isDeleting = before > count
            }
            doAfterTextChanged {
                debounceJob?.cancel()
                debounceJob = viewScope.launch {
                    val text = it.toString()
                    if (!isDeleting && text.lastOrNull() == '@') {
                        delay(Duration.ofMillis(300))
                        append(mailDomain)
                    }
                    if (listOf(' ', ',').any { symbol -> text.lastOrNull() == symbol }) {
                        delay(Duration.ofMillis(50))
                        val account = text.dropLast(1)
                        validateAccount(account)
                    }
                }
            }
            setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validateAccount(textView.text.toString())
                }
                false
            }
            setOnKeyListener { view, keyCode, event ->
                event?.let {
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                        if ((view as EditText).length() == 0 && chipGroup.childCount > 0) {
                            val chip = chipGroup.getChildAt(chipGroup.childCount - 1) as Chip
                            removeChipView(chip)
                        }
                    }
                }
                false
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    validateAccount(text.toString())
                }
            }
        }
        setBackgroundResource(R.drawable.rectangle_rounded_grey1)
        overScrollMode = OVER_SCROLL_NEVER
    }

    private fun validateAccount(mail: String) {
        if (mail.isNotEmpty() && recipientEditText.isEnabled && mail.contains('@')) {
            addAccountText(mail)
            recipientEditText.text.clear()
        }
    }

    private fun addAccountText(account: String) {
        (chipGroup.inflate(R.layout.mail_recipient_chip_layout) as Chip).apply {
            text = account
            setOnCloseIconClickListener {
                removeChipView(this@apply)
            }
            if (chipGroup.isGone) {
                chipGroup.isVisible = true
            }
            chipGroup.addView(this@apply)
        }
    }

    private fun removeChipView(chip: Chip) {
        chipGroup.removeView(chip)
        if (chipGroup.isEmpty()) {
            chipGroup.isGone = true
        }
    }

    fun setRecipientFixed(recipient: String) {
        recipientEditText.setText(recipient)
        recipientEditText.isEnabled = false
    }

    fun getRecipients() = chipGroup.children
        .mapNotNull { child ->
            child as? Chip
        }.map { chip ->
            chip.text.toString()
        }
        .distinct()
        .toList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        contentView.minimumWidth = viewWidth
        recipientEditText.minimumWidth = if (chipGroup.childCount > 0) {
            viewWidth * 2 / 10
        } else {
            viewWidth - convertDpToPx(30)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}