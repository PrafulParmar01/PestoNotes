package com.pesto.task.management.ui.tasks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.hideKeyboard
import com.pesto.task.management.base.extentions.toastShort
import com.pesto.task.management.databinding.ActivityAddTaskBinding
import com.pesto.task.management.ui.profile.UserModel
import com.pesto.task.management.utils.UtilsMethod


class AddTaskActivity : BaseActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private var strTitle = ""
    private var strDescription = ""
    private var strStatus = ""

    private lateinit var database: DatabaseReference
    private lateinit var getUserModel: UserModel

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AddTaskActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        getUserModel = UtilsMethod.convertStringToUserModel(applicationContext)
        binding.layoutToolBar.txtTitle.text = "Add Task"
        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        binding.txtStatus.setOnClickListener {
            showStatusDialog()
        }

        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                addTaskDetails()
            }
        }
    }


    private fun showStatusDialog() {
        val items = arrayOf("In Progress", "Done", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an status")
        builder.setSingleChoiceItems(items, -1) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    strStatus = "0"
                    binding.txtStatus.text="In-Progress"
                }

                1 -> {
                    dialog.dismiss()
                    strStatus = "1"
                    binding.txtStatus.text="Done"
                }

                else -> {
                    dialog.dismiss()
                    strStatus = "-1"
                    binding.txtStatus.text="Select Status"
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun isCheckValidation(): Boolean {
        var isCheck = false
        strTitle = binding.edtTitle.text.toString().trim()
        strDescription = binding.edtDescription.text.toString().trim()

        if (strTitle.isEmpty()) {
            toastShort("Please enter title")
            isCheck = true
        } else if (strDescription.isEmpty()) {
            toastShort("Please enter description")
            isCheck = true
        }
        else if (strStatus.isEmpty()) {
            toastShort("Please select status")
            isCheck = true
        }
        else if (strStatus == "-1") {
            toastShort("Please select status")
            isCheck = true
        }
        return isCheck
    }

    private fun addTaskDetails() {
        progressDialogs.showProgressDialog()
        val taskModel = TaskModel(
            title = strTitle,
            description = strDescription,
            status = strStatus,
            dateTime = UtilsMethod.dateFormatterCurrentDate(),
        )

        val instance = FirebaseDatabase.getInstance()
        database = instance.reference
        database.child("Tasks").push().setValue(taskModel)
            .addOnSuccessListener {
                progressDialogs.dismissDialog()
                toastShort("Task added successfully")
                finish()
            }.addOnFailureListener {
                progressDialogs.dismissDialog()
                Log.e("error : ","===> "+it.message.toString())
                toastShort("Something went wrong. Please try again later.")
            }
    }
}