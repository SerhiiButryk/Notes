/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.serhii.apps.notes.R

class AlertDialog(private val listener: ClickListener,
                  private val title: String,
                  private val message: String) : BaseDialogFragment() {

    @SuppressLint("InflateParams")
    override fun initView(inflater: LayoutInflater): ViewGroup {
        val dialogView = inflater.inflate(R.layout.alert_dialog, null) as ViewGroup

        val titleTv = dialogView.findViewById<TextView>(R.id.title_dialog)
        titleTv.text = title

        val messageTv = dialogView.findViewById<TextView>(R.id.message_text)
        messageTv.text = message

        val btnOk = dialogView.findViewById<TextView>(R.id.btn_ok)
        btnOk.setOnClickListener{
            listener.onConfirm()
            dismiss()
        }

        val btnCancel = dialogView.findViewById<TextView>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            listener.onCancel()
            dismiss()
        }

        return dialogView;
    }
}

interface ClickListener {
    fun onConfirm()
    fun onCancel()
}