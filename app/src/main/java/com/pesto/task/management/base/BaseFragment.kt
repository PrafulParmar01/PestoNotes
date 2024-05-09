package com.pesto.task.management.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.pesto.task.management.utils.JSDialogUtils

open class BaseFragment : Fragment() {

    lateinit var baseActivity: Activity
    lateinit var progressDialogs: JSDialogUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialogs = JSDialogUtils(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            baseActivity = (context as Activity)
        }
    }
}
