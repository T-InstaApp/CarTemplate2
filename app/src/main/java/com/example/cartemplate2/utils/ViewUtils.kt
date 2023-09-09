package com.example.cartemplate2.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.example.cartemplate2.R
import com.example.cartemplate2.ui.auth.LoginActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.sql.Timestamp
import java.util.*


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun Context.successToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun log(tag: String, message: String) {
    Log.d(tag, message)
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun CircularProgressIndicator.show() {
    visibility = View.VISIBLE
}

fun CircularProgressIndicator.hide() {
    visibility = View.GONE
}

fun RelativeLayout.visible() {
    visibility = View.VISIBLE
}

fun RelativeLayout.notVisible() {
    visibility = View.GONE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snakeBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.snakeBar2(message: String) {
    val snackBarView = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    val view = snackBarView.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.CENTER
    view.layoutParams = params
    params.width = ViewGroup.LayoutParams.WRAP_CONTENT
    val mainTextView =
        snackBarView.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    snackBarView.view.setBackgroundColor(Color.WHITE)
    mainTextView.setTextColor(
        Color.RED
    )
    mainTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    mainTextView.setTextSize(
        TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
    )
    snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackBarView.show()

}

fun showAlert(activity: Activity, title: String, message: String) {
    AlertDialog.Builder(activity)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            "Ok"
        ) { dialog, _ -> dialog?.dismiss() }.show()
}

fun currentTimeStamp(): String {
    //Date object
    val date = Date()
    //getTime() returns current time in milliseconds
    val time = date.time
    //Passed the milliseconds to constructor of Timestamp class
    val ts = Timestamp(time)
    return ts.toString()
}

fun createAlertDialogObject(activity: Activity): Dialog {
    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.alert_dialog)
    dialog.setCancelable(false)
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
    imgCancel.setOnClickListener { dialog.dismiss() }
    dialog.window!!.attributes = lp
    return dialog
}

fun getScreenWidthSize(activity: Activity): Double {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val height = windowMetrics.bounds.width() - insets.right - insets.left
        height.toDouble()
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.widthPixels
        height.toDouble()
    }
}

fun dialogSessionExpire(context: Context) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
    dialog.setContentView(R.layout.custom_dialog)

    val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
    txtHeading.text = context.resources.getString(R.string.Session_first_line)
    val txtMsg = dialog.findViewById<TextView>(R.id.txtMsg)
    txtMsg.text = context.resources.getString(R.string.Session_second_line)


    dialog.setCancelable(false)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        dialog.dismiss()
    }
    (dialog.findViewById<View>(R.id.imgCancel) as ImageView).visibility = View.GONE

    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    dialog.window!!.attributes = lp
}




