package com.pesto.task.management.ui.tasks

data class TaskModel(
    val title :String="",
    val description :String="",
    val status :String="",
    val dateTime :String="",
) {
    // Default no-argument constructor required by Firebase
    constructor() : this("", "", "","")
}