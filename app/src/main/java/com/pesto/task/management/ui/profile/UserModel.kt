package com.pesto.task.management.ui.profile

data class UserModel(
    val firstName :String="",
    val lastName :String="",
    val email :String="",
    val phone :String="",
) {
    // Default no-argument constructor required by Firebase
    constructor() : this("", "", "", "")
}