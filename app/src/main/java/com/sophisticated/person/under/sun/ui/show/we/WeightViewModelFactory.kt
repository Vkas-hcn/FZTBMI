package com.sophisticated.person.under.sun.ui.show.we

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophisticated.person.under.sun.data.show.BmiRepository
import com.sophisticated.person.under.sun.cc.BmiApp

class WeightViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeightViewModel::class.java)) {
            return WeightViewModel(BmiRepository(BmiApp.appContext)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}