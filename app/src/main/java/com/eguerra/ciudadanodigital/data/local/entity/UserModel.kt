package com.eguerra.ciudadanodigital.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserModel(
    @PrimaryKey val userId: Long,
    val email: String,
    val names: String,
    val lastnames: String,
    val birthdate: String,
    val phoneCode: String,
    val phoneNumber: String,
)