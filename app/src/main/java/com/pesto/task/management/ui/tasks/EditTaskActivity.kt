package com.pesto.task.management.ui.tasks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.pesto.task.management.R
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.hideKeyboard
import com.pesto.task.management.base.extentions.toastShort
import com.pesto.task.management.databinding.ActivityAddTaskBinding
import com.pesto.task.management.databinding.ActivityEditTaskBinding
import com.pesto.task.management.ui.profile.UserModel
import com.pesto.task.management.utils.UtilsMethod


class EditTaskActivity : BaseActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private var strOldTitle = ""
    private var strTitle = ""
    private var strDescription = ""
    private var strStatus = ""

    private lateinit var taskModel: TaskModel

    companion object {
        fun getIntent(context: Context, task: TaskModel): Intent {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("task", Gson().toJson(task))
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        val task = intent.getStringExtra("task") as String
        taskModel = UtilsMethod.convertStringToTaskModel(applicationContext, task)
        setDefaultData()

        binding.layoutToolBar.txtTitle.text = "Edit Task"
        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        binding.txtStatus.setOnClickListener {
            showStatusDialog()
        }

        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                updateTaskDetails()
            }
        }
    }

    private fun setDefaultData() {
        strOldTitle = taskModel.title
        strTitle = taskModel.title
        strDescription = taskModel.description
        strStatus = taskModel.status

        binding.edtTitle.setText(strTitle)
        binding.edtDescription.setText(strDescription)
        if (strStatus == "0") {
            binding.txtStatus.text = "In Progress"
        } else {
            binding.txtStatus.text = "Done"
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
                    binding.txtStatus.text = "In-Progress"
                }

                1 -> {
                    dialog.dismiss()
                    strStatus = "1"
                    binding.txtStatus.text = "Done"
                }

                else -> {
                    dialog.dismiss()
                    strStatus = "-1"
                    binding.txtStatus.text = "Select Status"
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
        } else if (strStatus.isEmpty()) {
            toastShort("Please select status")
            isCheck = true
        } else if (strStatus == "-1") {
            toastShort("Please select status")
            isCheck = true
        }
        return isCheck
    }

    private fun updateTaskDetails() {
        progressDialogs.showProgressDialog()
        val instance = FirebaseDatabase.getInstance()
        val database = instance.reference
        val query = database.child("Tasks").orderByChild("title").equalTo(strOldTitle)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val key = snapshot.key
                    key?.let {
                        val taskRef = database.child("Tasks").child(it)
                        val updatedTask = TaskModel(
                            title = strTitle,
                            description = strDescription,
                            status = strStatus,
                            dateTime = UtilsMethod.dateFormatterCurrentDate(),
                        )
                        taskRef.setValue(updatedTask)
                            .addOnSuccessListener {
                                progressDialogs.dismissDialog()
                                toastShort("Task updated successfully")
                                finish()
                            }
                            .addOnFailureListener { e ->
                                progressDialogs.dismissDialog()
                                toastShort("Something went wrong. Please try again later.")
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressDialogs.dismissDialog()
                toastShort("Failed to update task. Please try again later.")
            }
        })
    }
}