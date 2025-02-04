package com.example.lrcd_r

data class Reservations(
    val refnum: String ?= null,
    val date: String ?= null,
    val stime: String ?= null,
    val etime: String ?= null,
    val roomNum: String ?= null,
    val userType: String ?= null,
    val name: String ?= null,
    val dept: String ?= null,
    val id: String ?= null,
    val email: String ?= null,
    val cnum: String ?= null,
    val tableCount: String ?= null,
    val chairCount: String ?= null,
    val purpose: String ?= null,
    val otherMaterials: String ?= null,
)