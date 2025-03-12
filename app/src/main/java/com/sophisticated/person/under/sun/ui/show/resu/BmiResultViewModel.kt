package com.sophisticated.person.under.sun.ui.show.resu

import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.utils.BmiUtils

class BmiResultViewModel : androidx.lifecycle.ViewModel() {

    private val _bmiResultData = androidx.lifecycle.MutableLiveData<BmiResultData>()
    val bmiResultData: androidx.lifecycle.LiveData<BmiResultData> = _bmiResultData

    fun processBmiData(height: String, weight: String, remark: String, timestamp: Long) {
        val bmiValue = BmiUtils.calculateBMI(height, weight)
        val bmiState = BmiUtils.calculateBMIState(height, weight)
        val bmiStateCode = BmiUtils.calculateBMIState(bmiState)
        val stateColorResId = BmiUtils.calculateBMIColor(bmiState)
        val recommendationResId = getRecommendationResId(bmiStateCode)
        val formattedDate = BmiUtils.formatTimestamp(timestamp)

        _bmiResultData.value = BmiResultData(
            bmiValue = bmiValue,
            bmiState = bmiState,
            bmiStateCode = bmiStateCode,
            stateColorResId = stateColorResId,
            recommendationResId = recommendationResId,
            formattedDate = formattedDate,
            remark = remark
        )
    }

    private fun getRecommendationResId(bmiStateCode: Int): Int {
        return when(bmiStateCode) {
            1 -> R.string.o_w_1
            2 -> R.string.o_w_2
            3 -> R.string.o_w_3
            4 -> R.string.o_w_4
            else -> R.string.o_w_2
        }
    }
}