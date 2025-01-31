package com.example.lrcd_r

data class User(
    val gname: String ?= null,
    val lname: String ?= null,
    val mname: String ?= null,
    val userType: String ?= null,
    val dept: String ?= null,
    val id: String ?= null,
    val email: String ?= null,
    val password: String ?= null
)
