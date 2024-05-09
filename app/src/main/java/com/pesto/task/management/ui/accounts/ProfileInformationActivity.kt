package com.pesto.task.management.ui.accounts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.hideKeyboard
import com.pesto.task.management.base.extentions.toastShort
import com.pesto.task.management.databinding.ActivityProfileInformationBinding
import com.pesto.task.management.ui.profile.UserModel
import com.pesto.task.management.utils.PrefUtil
import com.pesto.task.management.utils.UtilsMethod


class ProfileInformationActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileInformationBinding
    private var strFirstName = ""
    private var strLastName = ""
    private var strEmail = ""
    private var strPhone = ""

    private lateinit var getUserModel: UserModel

    private lateinit var database: DatabaseReference

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileInformationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        getUserModel = UtilsMethod.convertStringToUserModel(applicationContext)

        binding.layoutToolBar.txtTitle.text = "Profile Information"
        binding.layoutToolBar.btnEdit.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE

        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        binding.layoutToolBar.btnEdit.setOnClickListener {
            setEditEnabled()
            binding.btnSave.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                updateProfileDetails()
            }
        }

        setDefaultData()
        setEditDisabled()
    }

    private fun setEditDisabled() {
        binding.edtFirstName.isEnabled = false
        binding.edtLastName.isEnabled = false
    }

    private fun setEditEnabled() {
        binding.edtFirstName.isEnabled = true
        binding.edtLastName.isEnabled = true
    }


    private fun setDefaultData() {
        strFirstName = getUserModel.firstName
        strLastName = getUserModel.lastName
        strEmail = getUserModel.email
        strPhone = getUserModel.phone

        binding.edtFirstName.setText(strFirstName)
        binding.edtLastName.setText(strLastName)
    }



    private fun isCheckValidation(): Boolean {
        var isCheck = false
        strFirstName = binding.edtFirstName.text.toString()
        strLastName = binding.edtLastName.text.toString()

        if (strFirstName.isEmpty()) {
            toastShort("Please enter first name")
            isCheck = true
        } else if (strLastName.isEmpty()) {
            toastShort("Please enter last name")
            isCheck = true
        }
        return isCheck
    }

    private fun updateProfileDetails() {
        progressDialogs.showProgressDialog()
        val userModel = UserModel(
            firstName = strFirstName,
            lastName = strLastName,
            email = strEmail,
            phone = strPhone,
        )

        val instance = FirebaseDatabase.getInstance()
        database = instance.reference
        database.child("Users").child(getUserModel.phone).setValue(userModel)
            .addOnSuccessListener {
                progressDialogs.dismissDialog()
                toastShort("Profile updated successfully")
                PrefUtil.putStringPref(PrefUtil.PREF_USER_MODEL, Gson().toJson(userModel),applicationContext)
                finish()
            }.addOnFailureListener {
                progressDialogs.dismissDialog()
                Log.e("error : ","===> "+it.message.toString())
                toastShort("Something went wrong. Please try again later.")
            }
    }
}