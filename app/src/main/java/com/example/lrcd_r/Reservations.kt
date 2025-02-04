package com.example.lrcd_r

data class Reservations(
    val userID: String ?= null,
    val reservationDate: String ?= null,

    //4
    val date: String ?= null,
    val stime: String ?= null,
    val etime: String ?= null,
    val roomNum: String ?= null,

    //5
    val cnum: String ?= null,
    val tableCount: String ?= null,
    val chairCount: String ?= null,
    val purpose: String ?= null,
    val otherMaterials: String ?= null
)