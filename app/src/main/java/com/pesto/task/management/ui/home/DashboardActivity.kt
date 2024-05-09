package com.pesto.task.management.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.pesto.task.management.base.BaseActivity
import com.pesto.task.management.base.extentions.startActivityWithFadeInAnimation
import com.pesto.task.management.base.extentions.toastShort
import com.pesto.task.management.databinding.ActivityHomeBinding
import com.pesto.task.management.ui.accounts.AccountsActivity
import com.pesto.task.management.ui.profile.UserModel
import com.pesto.task.management.ui.tasks.AddTaskActivity
import com.pesto.task.management.ui.tasks.EditTaskActivity
import com.pesto.task.management.ui.tasks.TaskModel
import com.pesto.task.management.utils.UtilsMethod


class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var taskListAdapter: TaskListAdapter
    private var listOfData: MutableList<TaskModel> = mutableListOf()
    private lateinit var getUserModel: UserModel

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserModel = UtilsMethod.convertStringToUserModel(applicationContext)

        binding.btnAccount.setOnClickListener {
            startActivityWithFadeInAnimation(AccountsActivity.getIntent(this))
        }

        binding.btnAddTasks.setOnClickListener {
            startActivityWithFadeInAnimation(AddTaskActivity.getIntent(this))
        }

        binding.recycleViewList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        taskListAdapter = TaskListAdapter(this)
        binding.recycleViewList.adapter = taskListAdapter

        taskListAdapter.onClickArrow = { task ->
            onConfirmationDialog(task)
        }

        taskListAdapter.onEditClick = { task ->
            startActivityWithFadeInAnimation(EditTaskActivity.getIntent(this,task))
        }
    }

    private fun onConfirmationDialog(task: TaskModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.setPositiveButton("DELETE") { dialog, which ->
            dialog.dismiss()
            onDeleteItem(task)
        }

        builder.setNegativeButton("NO") { dialog, which ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun onDeleteItem(task: TaskModel) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Tasks")
        val query: Query = myRef.orderByChild("title").equalTo(task.title)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val key = snapshot.key
                    myRef.child(key!!).removeValue()
                        .addOnSuccessListener {
                            toastShort("Task deleted successfully")
                        }
                        .addOnFailureListener {
                            toastShort("Something went wrong while delete task")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                toastShort("Something went wrong while delete task")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        onSyncReadData()
    }

    private fun onSyncReadData() {
        progressDialogs.showProgressDialog()
        val firebase = FirebaseDatabase.getInstance()
        val reference = firebase.reference
        reference.child("Tasks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfData.clear()
                    for (childSnapshot in snapshot.children) {
                        val receivedData: TaskModel? = childSnapshot.getValue(TaskModel::class.java)
                        if (receivedData != null) {
                            listOfData.add(receivedData)
                        }
                    }

                    if (listOfData.isNotEmpty()) {
                        binding.recycleViewList.visibility = View.VISIBLE
                        binding.txtNoData.visibility = View.GONE
                        taskListAdapter.onUpdateList(listOfData)
                    } else {
                        binding.recycleViewList.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                    progressDialogs.dismissDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    toastShort("Something went wrong. Please try again later.")
                }
            })
    }
}