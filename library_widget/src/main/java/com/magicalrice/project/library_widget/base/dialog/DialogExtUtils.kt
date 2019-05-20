package com.magicalrice.project.library_widget.base.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.magicalrice.project.library_base.base.ScreenUtils
import com.magicalrice.project.library_widget.R

object DialogExtUtils {
    fun showLoadingDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context, R.style.LoadingDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_loading, null)
        val dialog = builder.create()
        dialog.show()
        val params = dialog.window?.attributes
        params?.width = ScreenUtils.dp2pxInt(context, 82)
        params?.height = ScreenUtils.dp2pxInt(context, 82)
        dialog.window?.attributes = params
        dialog.window?.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window?.setGravity(Gravity.CENTER)
        var animation: Animation? = AnimationUtils.loadAnimation(context, R.anim.loading_rotate)
        view.findViewById<ImageView>(R.id.img_loading).startAnimation(animation)
        dialog.setOnDismissListener {
            animation?.cancel()
            animation = null
        }
        return dialog
    }

    fun showDialog(
        context: Context,
        title: String,
        content: String,
        cancelText: String,
        sureText: String,
        cancelListener: View.OnClickListener?,
        confirmListener: View.OnClickListener?
    ) {
        val builder = AlertDialog.Builder(context, R.style.BottomDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_custom, null)
        val dialog = builder.create()
        dialog.show()
        val params = dialog.window?.attributes
        params?.width = ScreenUtils.dp2pxInt(context, 300)
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = params
        if (title.isEmpty()) {
            view.findViewById<TextView>(R.id.dialog_title).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.dialog_title).text = ""
        } else {
            view.findViewById<TextView>(R.id.dialog_title).text = title
        }

        if (content.isEmpty()) {
            view.findViewById<TextView>(R.id.dialog_content).visibility = View.GONE
            view.findViewById<TextView>(R.id.dialog_content).text = ""
        } else {
            view.findViewById<TextView>(R.id.dialog_content).text = content
        }

        if (cancelText.isEmpty()) {
            view.findViewById<TextView>(R.id.dialog_btn_cancel).text = ""
            view.findViewById<TextView>(R.id.dialog_btn_cancel).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.dialog_btn_cancel).text = cancelText
        }

        if (sureText.isEmpty()) {
            view.findViewById<TextView>(R.id.dialog_btn_sure).text = ""
        } else {
            view.findViewById<TextView>(R.id.dialog_btn_sure).text = sureText
        }

        if (cancelListener == null) {
            view.findViewById<TextView>(R.id.dialog_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
        } else {
            view.findViewById<TextView>(R.id.dialog_btn_cancel).setOnClickListener {
                cancelListener.onClick(it)
                dialog.dismiss()
            }
        }

        if (confirmListener == null) {
            view.findViewById<TextView>(R.id.dialog_btn_sure).setOnClickListener {
                dialog.dismiss()
            }
        } else {
            view.findViewById<TextView>(R.id.dialog_btn_sure).setOnClickListener {
                confirmListener.onClick(it)
                dialog.dismiss()
            }
        }
        dialog.window?.setContentView(view)
        dialog.window?.setGravity(Gravity.CENTER)
    }
}