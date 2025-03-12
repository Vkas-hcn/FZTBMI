package com.sophisticated.person.under.sun.ui.show.resu

data class BmiResultData(
    val bmiValue: String,
    val bmiState: String,
    val bmiStateCode: Int,
    val stateColorResId: Int,
    val recommendationResId: Int,
    val formattedDate: String,
    val remark: String
)