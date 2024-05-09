package com.pesto.task.management.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pesto.task.management.utils.JSDialogUtils

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext: Activity
    lateinit var progressDialogs: JSDialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        progressDialogs = JSDialogUtils(this)
    }
}