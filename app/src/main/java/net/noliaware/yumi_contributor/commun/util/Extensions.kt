package net.noliaware.yumi_contributor.commun.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.FlowCollector
import net.noliaware.yumi_contributor.BuildConfig
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.*
import net.noliaware.yumi_contributor.commun.data.remote.dto.AppMessageDTO
import net.noliaware.yumi_contributor.commun.data.remote.dto.ErrorDTO
import net.noliaware.yumi_contributor.commun.data.remote.dto.SessionDTO
import net.noliaware.yumi_contributor.commun.domain.model.AppMessageType
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.feature_login.presentation.controllers.LoginActivity
import java.io.Serializable
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun generateToken(timestamp: String, methodName: String, randomString: String): String {
    return "noliaware|$timestamp|${methodName}|${timestamp.reversed()}|$randomString".sha256()
}

fun getCommonWSParams(sessionData: SessionData, tokenKey: String) = mapOf(
    LOGIN to sessionData.login,
    APP_VERSION to BuildConfig.VERSION_NAME,
    DEVICE_ID to sessionData.deviceId,
    SESSION_ID to sessionData.sessionId,
    SESSION_TOKEN to sessionData.sessionTokens[tokenKey].toString()
)

suspend fun <T> FlowCollector<Resource<T>>.handleSessionWithNoFailure(
    session: SessionDTO?,
    sessionData: SessionData,
    tokenKey: String,
    appMessage: AppMessageDTO?,
    error: ErrorDTO?
): Boolean {

    val errorType = session?.let { sessionDTO ->
        sessionData.apply {
            sessionId = sessionDTO.sessionId
            sessionTokens[tokenKey] = sessionDTO.sessionToken
        }

        ErrorType.RECOVERABLE_ERROR
    } ?: run {
        ErrorType.SYSTEM_ERROR
    }

    error?.let { errorDTO ->
        emit(
            Resource.Error(
                errorType = errorType,
                errorMessage = errorDTO.errorMessage,
                appMessage = appMessage?.toAppMessage()
            )
        )
        return false
    } ?: run {
        return true
    }
}

fun handlePaginatedListErrorIfAny(
    session: SessionDTO?,
    sessionData: SessionData,
    tokenKey: String
): ErrorType {
    val errorType = session?.let { sessionDTO ->
        sessionData.apply {
            sessionId = sessionDTO.sessionId
            sessionTokens[tokenKey] = sessionDTO.sessionToken
        }
        ErrorType.RECOVERABLE_ERROR
    } ?: run {
        ErrorType.SYSTEM_ERROR
    }
    return errorType
}

fun parseToShortDate(dateStr: String?) = dateStr?.let {
    val sourceFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
    val date = sourceFormatter.parse(dateStr)
    val destFormatter = SimpleDateFormat("dd LLL yyyy", Locale.FRANCE)
    destFormatter.format(date)
}.orEmpty()

fun parseToLongDate(dateStr: String?) = dateStr?.let {
    val sourceFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
    val date = sourceFormatter.parse(dateStr)
    val destFormatter = SimpleDateFormat("dd LLLL yyyy", Locale.FRANCE)
    destFormatter.format(date)
}.orEmpty()

fun parseTimeString(dateStr: String?) = dateStr?.let {
    val sourceFormatter = SimpleDateFormat("HH:mm:ss", Locale.FRANCE)
    val date = sourceFormatter.parse(dateStr)
    val destFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)
    destFormatter.format(date)
}.orEmpty()

fun parseTimestampToString(periodInSeconds: Int?) = periodInSeconds?.let {
    val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.FRANCE)
    simpleDateFormat.format(periodInSeconds * 1000L)
}.orEmpty()

fun Fragment.handleSharedEvent(sharedEvent: UIEvent) = context?.let {

    when (sharedEvent) {

        is UIEvent.ShowAppMessage -> {

            val appMessage = sharedEvent.appMessage
            when (appMessage.type) {
                AppMessageType.POPUP -> {
                    MaterialAlertDialogBuilder(it)
                        .setTitle(appMessage.title)
                        .setMessage(appMessage.body)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create().apply {
                            setCanceledOnTouchOutside(false)
                            show()
                        }
                }

                AppMessageType.SNACKBAR -> {
                    Snackbar.make(
                        requireView(),
                        appMessage.body,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                AppMessageType.TOAST -> {
                    Toast.makeText(
                        context,
                        appMessage.body,
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> Unit
            }
        }

        is UIEvent.ShowError -> {
            Toast.makeText(
                context,
                getString(sharedEvent.errorStrRes),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

fun Fragment.redirectToLoginScreenFromSharedEvent(sharedEvent: UIEvent) {
    if (sharedEvent is UIEvent.ShowError) {
        if (sharedEvent.errorType == ErrorType.SYSTEM_ERROR) {
            redirectToLoginScreenInternal()
        }
    }
}

fun Fragment.handlePaginationError(loadState: CombinedLoadStates) {
    when (val currentState = loadState.refresh) {
        is LoadState.Loading -> Unit
        is LoadState.Error -> {
            if (
                currentState.error is PaginationException &&
                (currentState.error as PaginationException).errorType == ErrorType.SYSTEM_ERROR
            ) {
                redirectToLoginScreenInternal()
            }
        }
        else -> Unit
    }
}

private fun Fragment.redirectToLoginScreenInternal() {
    activity?.finish()
    startActivity(Intent(requireActivity(), LoginActivity::class.java))
}

fun <T : Serializable?> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        (getSerializable(key) as T)
    }

inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }

fun <T : Fragment> T.withArgs(vararg pairs: Pair<String, Any?>) =
    apply { arguments = bundleOf(*pairs) }

fun ViewGroup.inflate(
    layoutRes: Int,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun Context.drawableIdByName(resIdName: String?): Int {
    resIdName?.let {
        return resources.getIdentifier(it, "drawable", packageName)
    }
    throw Resources.NotFoundException()
}

fun View.getStatusBarHeight() = convertDpToPx(24)

fun View.measureWrapContent() {
    measure(
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    )
}

fun View.layoutToTopLeft(left: Int, top: Int) {
    val right = left + measuredWidth
    val bottom = top + measuredHeight
    layout(left, top, right, bottom)
}

fun View.layoutToTopRight(right: Int, top: Int) {
    val left = right - measuredWidth
    val bottom = top + measuredHeight
    layout(left, top, right, bottom)
}

fun View.layoutToBottomLeft(left: Int, bottom: Int) {
    val right = left + measuredWidth
    val top = bottom - measuredHeight
    layout(left, top, right, bottom)
}

fun View.layoutToBottomRight(right: Int, bottom: Int) {
    val left = right - measuredWidth
    val top = bottom - measuredHeight
    layout(left, top, right, bottom)
}

fun View.convertDpToPx(dpValue: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), context.resources.displayMetrics
).toInt()

fun View.getLocationRectOnScreen(): Rect {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Rect().apply {
        left = location[0]
        top = location[1]
        right = left + measuredWidth
        bottom = top + measuredHeight
    }
}

fun ViewPager2.removeOverScroll() {
    (getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
}

@JvmOverloads
@Dimension(unit = Dimension.PX)
fun Number.dpToPx(
    metrics: DisplayMetrics = Resources.getSystem().displayMetrics
): Float {
    return toFloat() * metrics.density
}

@JvmOverloads
@Dimension(unit = Dimension.DP)
fun Number.pxToDp(
    metrics: DisplayMetrics = Resources.getSystem().displayMetrics
): Float {
    return toFloat() / metrics.density
}

fun Context.showKeyboard() {
    (this as? Activity)?.let {
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    }
}

fun Context.hideKeyboard() {
    (this as? Activity)?.let {
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }
}

inline fun <reified T : View> View.find(id: Int): T = findViewById(id)
inline fun <reified T : View> Activity.find(id: Int): T = findViewById(id)
inline fun <reified T : View> Fragment.find(id: Int): T = view?.findViewById(id) as T
inline fun <reified T : View> RecyclerView.ViewHolder.find(id: Int): T =
    itemView.findViewById(id) as T

inline fun <reified T : View> View.findOptional(id: Int): T? = findViewById(id) as? T
inline fun <reified T : View> Activity.findOptional(id: Int): T? = findViewById(id) as? T
inline fun <reified T : View> Fragment.findOptional(id: Int): T? = view?.findViewById(id) as? T
inline fun <reified T : View> RecyclerView.ViewHolder.findOptional(id: Int): T? =
    itemView.findViewById(id) as? T

fun String.sha256(): String {
    return try {
        val md = MessageDigest.getInstance("SHA-256")
        val messageDigest = md.digest(this.toByteArray(StandardCharsets.UTF_8))
        val number = BigInteger(1, messageDigest)
        val hexString = StringBuilder(number.toString(16))
        while (hexString.length < 64) {
            hexString.insert(0, '0')
        }
        hexString.toString()

    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException(e)
    }
}

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

@ColorInt
fun String.parseHexColor(): Int {
    return if (isEmpty()) {
        Color.TRANSPARENT
    } else {
        Color.parseColor(this)
    }
}

fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
    AppCompatResources.getDrawable(this, drawableRes)

@CheckResult
fun Drawable.tint(@ColorInt color: Int): Drawable {
    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(tintedDrawable, color)
    return tintedDrawable
}

@CheckResult
fun Drawable.tint(context: Context, @ColorRes color: Int): Drawable {
    return tint(context.getColorCompat(color))
}

fun Number.formatNumber(): String = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)

fun String.decorateText(
    coloredText1: String,
    color1: Int,
    coloredText2: String,
    color2: Int
) = SpannableString(this).apply {
    val colorSpan1 = ForegroundColorSpan(color1)
    val startIndex1 = indexOf(coloredText1)
    val endIndex1 = startIndex1 + coloredText1.length
    setSpan(
        colorSpan1,
        startIndex1,
        endIndex1,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val colorSpan2 = ForegroundColorSpan(color2)
    val startIndex2 = indexOf(coloredText2)
    val endIndex2 = startIndex2 + coloredText2.length
    setSpan(
        colorSpan2,
        startIndex2,
        endIndex2,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
val <T> T.exhaustive: T get() = this

fun openMap(
    context: Context?,
    latitude: String?,
    longitude: String?,
    label: String?
) {
    val uriBuilder = Uri.Builder()
        .scheme("geo")
        .path("0,0")
        .appendQueryParameter("q", "$latitude,$longitude($label)")
    val mapIntent = Intent(Intent.ACTION_VIEW, uriBuilder.build())
    try {
        context?.startActivity(mapIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            context?.applicationContext,
            R.string.application_not_found,
            Toast.LENGTH_LONG
        ).show();
    }
}

fun Context.startWebBrowserAtURL(url: String) {
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }.run {
        startActivity(this)
    }
}

fun Context.openWebPage(url: String): Boolean {
    // Format the URI properly.
    val uri = url.toWebUri()

    // Try using Chrome Custom Tabs.
    try {

        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(getColorCompat(R.color.colorPrimary))
            .build()

        val intent = CustomTabsIntent.Builder()
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
            .setShowTitle(true)
            .build()
        intent.launchUrl(this, uri)
        return true
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    // Fall back to launching a default web browser intent.
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            return true
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    // We were unable to show the web page.
    return false
}

fun String.toWebUri(): Uri {
    return (
            if (startsWith("http://") || startsWith("https://"))
                this
            else
                "https://$this"
            ).toUri()
}

fun Context.makeCall(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    startActivity(intent)
}

fun Context.toActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}