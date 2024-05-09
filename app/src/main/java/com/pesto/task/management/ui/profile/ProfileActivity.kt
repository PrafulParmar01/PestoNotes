package com.pesto.task.management.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.hideKeyboard
import com.pesto.task.management.base.extentions.startActivityWithFadeInAnimation
import com.pesto.task.management.base.extentions.toastShort
import com.pesto.task.management.databinding.ActivityProfileBinding
import com.pesto.task.management.ui.home.DashboardActivity
import com.pesto.task.management.utils.PrefUtil


class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var strFirstName = ""
    private var strLastName = ""
    private var strEmail = ""
    private var strPhone = ""

    private lateinit var database: DatabaseReference

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        strPhone = PrefUtil.getStringPref(PrefUtil.PREF_PHONE_NUMBER, applicationContext)
        binding.edtPhone.text = strPhone

        binding.lToolbar.txtTitle.text = "Profile Information"
        binding.lToolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                insertProfileDetails()
            }
        }
    }



    private fun isCheckValidation(): Boolean {
        var isCheck = false
        strFirstName = binding.edtFirstName.text.toString()
        strLastName = binding.edtLastName.text.toString()
        strEmail = binding.edtEmail.text.toString()
        strPhone = binding.edtPhone.text.toString()

        if (strFirstName.isEmpty()) {
            toastShort("Please enter first name")
            isCheck = true
        }
        if (strLastName.isEmpty()) {
            toastShort("Please enter first name")
            isCheck = true
        }
        else if (strEmail.isEmpty()) {
            toastShort("Please email address")
            isCheck = true
        } else if (strPhone.isEmpty()) {
            toastShort("Please enter phone number")
            isCheck = true
        }
        return isCheck
    }

    private fun insertProfileDetails() {
        progressDialogs.showProgressDialog()
        val userModel = UserModel(
            firstName = strFirstName,
            lastName = strLastName,
            email = strEmail,
            phone = strPhone,
        )

        val instance = FirebaseDatabase.getInstance()
        database = instance.reference
        database.child("Users").child(strPhone).setValue(userModel)
            .addOnSuccessListener {
                progressDialogs.dismissDialog()
                toastShort("Profile details added successfully")
                PrefUtil.putStringPref(PrefUtil.PREF_USER_MODEL, Gson().toJson(userModel),applicationContext)
                PrefUtil.putBooleanPref(PrefUtil.PREF_IS_PROFILE_FILLED, true, applicationContext)
                val intent = DashboardActivity.getIntent(this)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityWithFadeInAnimation(intent)
                finish()
            }.addOnFailureListener {
                progressDialogs.dismissDialog()
                Log.e("error : ","===> "+it.message.toString())
                toastShort("Something went wrong. Please try again later.")
            }
    }
}