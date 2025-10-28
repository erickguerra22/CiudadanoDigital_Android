package com.eguerra.ciudadanodigital.data.remote.dto

import com.eguerra.ciudadanodigital.data.local.entity.UserModel


data class UserDto(
    val userid: Long,
    val email: String,
    val names: String,
    val lastnames: String,
    val birthdate: String,
    val phonecode: String,
    val phonenumber: String
)

fun UserDto.toUserModel(): UserModel {
    return UserModel(
        userId = this.userid,
        email = this.email,
        names = this.names,
        lastnames = this.lastnames,
        birthdate = this.birthdate,
        phoneCode = this.phonecode,
        phoneNumber = this.phonenumber
    )
}