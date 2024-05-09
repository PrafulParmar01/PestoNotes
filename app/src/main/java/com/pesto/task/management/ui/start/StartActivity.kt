package com.pesto.task.management.ui.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.startActivityWithFadeInAnimation
import com.pesto.task.management.databinding.ActivityStartBinding
import com.pesto.task.management.ui.home.DashboardActivity
import com.pesto.task.management.ui.login.LoginActivity
import com.pesto.task.management.utils.PrefUtil


class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            val isLoginEnabled = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_LOGIN, applicationContext)
            if (isLoginEnabled) {
                startActivityWithFadeInAnimation(DashboardActivity.getIntent(this))
                finish()
            } else {
                startActivityWithFadeInAnimation(LoginActivity.getIntent(this))
                finish()
            }
        }, 2000L)
    }
}