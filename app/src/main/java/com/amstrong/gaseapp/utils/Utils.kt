package com.amstrong.gaseapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.view.View
import androidx.fragment.app.Fragment
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.ui.recycler_rows.LineItemWithTaxesRow
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun <A : Activity> Activity.startActivity(activity: Class<A>){
    Intent(this, activity).also {
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun Int.toDecimalFormat() : String{
    return NumberFormat.getInstance(Locale.FRENCH).format(this)
//    val current: Locale = getResources().getConfiguration().locale
//    return NumberFormat.getInstance(Locale.getDefault()).format(this)
}

fun Float.toDecimalFormat() : String{
    return NumberFormat.getInstance(Locale.FRENCH).format(this)
//    val current: Locale = getResources().getConfiguration().locale
//    return NumberFormat.getInstance(Locale.getDefault()).format(this)
}

fun Long.toDecimalFormat() : String{
    return NumberFormat.getInstance(Locale.FRENCH).format(this)
//    val current: Locale = getResources().getConfiguration().locale
//    return NumberFormat.getInstance(Locale.getDefault()).format(this)
}

fun Long.toLocalDateTime() : LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun LocalDateTime.toDate() : Date{
    return Date.from(this
        .atZone(ZoneId.systemDefault())
        .toInstant())
}

fun String.reverseDecimalFormat(): String {
    return this.replace(DecimalFormatSymbols(Locale.FRENCH).groupingSeparator.toString().toRegex(), "")
}


fun Date.formatToPattern(pattern: String = "d MMM yyyy HH:mm:ss") : String{
    val sdfDate = SimpleDateFormat(pattern, Locale.getDefault())

    return sdfDate.format(this)
//    val current: Locale = getResources().getConfiguration().locale
//    return NumberFormat.getInstance(Locale.getDefault()).format(this)
}


fun Context.runningOnTablet() : Boolean{
    val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return (Objects.requireNonNull(manager).phoneType == TelephonyManager.PHONE_TYPE_NONE)
}


fun List<LineItem>.toLineItemWithTaxes(): List<LineItemWithTaxes> {
    return this.map {
        LineItemWithTaxes(it, it.line_taxes)
    }
}


fun List<LineItemWithTaxes>.toLineItem(): List<LineItem> {
    return this.map {
        LineItem(it.lineItem, it.lineTaxes)
    }
}

fun LineItemWithTaxes.toLineItem(): LineItem {
    return LineItem(this.lineItem, this.lineItem.quantity, this.lineTaxes)
}

fun LineItem.toLineItemWithTaxes(): LineItemWithTaxes {
    return LineItemWithTaxes(this, this.line_taxes)
}




fun View.snackbarLengthIndefinite(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
        failure: Resource.Failure,
        retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> requireView().snackbar(
                "Veuillez vérifier votre accès au réseau",
                retry
        )
        /*
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("You've entered incorrect email or password")
            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        */
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbarLengthIndefinite(error)
        }
    }
}

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}


inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}


const val EXTRA_RECEIPT_ID = "com.amstrong.gaseapp.MESSAGE_RECEIPT_ID"
