package com.sophisticated.person.under.sun.ui.show.plus


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sophisticated.person.under.sun.data.show.BmiBean
import com.sophisticated.person.under.sun.data.show.BmiRepository
import com.sophisticated.person.under.sun.cc.BmiApp

class AddViewModel : ViewModel() {

    private val bmiRepository = BmiRepository(BmiApp.appContext)

    private val _navigateToResult = MutableLiveData<BmiBean>()
    val navigateToResult: LiveData<BmiBean> get() = _navigateToResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveBmiRecord(bmiRecord: BmiBean) {
        try {
            bmiRepository.addRecord(bmiRecord)
            _navigateToResult.value = bmiRecord
        } catch (e: Exception) {
            _errorMessage.value = "Failed to save BMI record: ${e.message}"
        }
    }
}