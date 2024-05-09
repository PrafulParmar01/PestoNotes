package com.pesto.task.management.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pesto.task.management.ui.profile.UserModel
import com.pesto.task.management.ui.tasks.TaskModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


open class UtilsMethod {

    companion object {

        fun convertSeconds(input: Long): String {
            return if (input >= 10L) {
                input.toString()
            } else {
                "0$input"
            }
        }

        fun convertStringToUserModel(mContext: Context): UserModel {
            val userJson = PrefUtil.getStringPref(PrefUtil.PREF_USER_MODEL, mContext)
            val tokenType = object : TypeToken<UserModel>() {}.type
            return Gson().fromJson(userJson, tokenType)
        }

        fun convertStringToTaskModel(mContext: Context,json:String): TaskModel {
            val tokenType = object : TypeToken<TaskModel>() {}.type
            return Gson().fromJson(json, tokenType)
        }

        fun dateFormatterCurrentDate(): String{
            var returnDate = ""
            try {
                val sdfTime = SimpleDateFormat("dd MMM yyyy hh:mm:ss aa", Locale.getDefault())
                returnDate = sdfTime.format(Date())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return returnDate
        }
    }


}