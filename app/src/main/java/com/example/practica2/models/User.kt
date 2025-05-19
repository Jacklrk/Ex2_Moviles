package com.example.practica2.models

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var fcmToken: String = ""
)
