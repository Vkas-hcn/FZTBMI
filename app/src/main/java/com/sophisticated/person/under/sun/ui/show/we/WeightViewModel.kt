package com.sophisticated.person.under.sun.ui.show.we

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sophisticated.person.under.sun.data.show.BmiRepository
import com.sophisticated.person.under.sun.data.show.UserWeightBean
import com.sophisticated.person.under.sun.utils.BmiUtils

class WeightViewModel(private val repository: BmiRepository) : ViewModel() {

    private val _weightRecords = MutableLiveData<MutableList<UserWeightBean>>()
    val weightRecords: LiveData<MutableList<UserWeightBean>> = _weightRecords

    private val _isKiloUnit = MutableLiveData<Boolean>()
    val isKiloUnit: LiveData<Boolean> = _isKiloUnit

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private var editingTimestamp: Long = 0L

    init {
        _isKiloUnit.value = BmiUtils.getBmiUnit()
    }

    fun loadWeightRecords() {
        val records = repository.getAllWeightRecords().toMutableList()

        // 单位转换
        if (_isKiloUnit.value == true) {
            convertToKiloUnit(records)
        }

        _weightRecords.value = records
    }

    fun toggleUnits() {
        var state = BmiUtils.getBmiUnit()
        BmiUtils.saveBmiUnit(!state)
        _isKiloUnit.value = BmiUtils.getBmiUnit()

        // 重新加载数据以应用新单位
        loadWeightRecords()
    }

    fun setEditingTimestamp(timestamp: Long) {
        editingTimestamp = timestamp
    }

    fun saveWeightRecord(weightText: String, remark: String): Boolean {
        // 验证输入
        if (weightText.isBlank() || !BmiUtils.isValidNumber(weightText)) {
            _toastMessage.value = "Please enter your weight"
            return false
        }

        val weightBean = UserWeightBean().apply {
            // 根据当前单位进行转换
            weight = if (_isKiloUnit.value == true) {
                (weightText.toDouble() / 2.2046).toString()
            } else {
                weightText
            }
            this.remark = remark
        }

        // 如果是编辑模式
        if (editingTimestamp != 0L) {
            weightBean.timestamp = editingTimestamp
            repository.editWeightRecord(weightBean)
        } else {
            weightBean.timestamp = System.currentTimeMillis()
            repository.addWeightRecord(weightBean)
        }
        _toastMessage.value = "Successfully saved"
        loadWeightRecords()
        return true
    }


    fun deleteWeightRecord(bean: UserWeightBean) {
        repository.deleteWeightRecord(bean.timestamp)
        loadWeightRecords()
    }

    fun calculateWeightStats(records: List<UserWeightBean>): Map<String, Double> {
        if (records.isEmpty()) {
            return mapOf(
                "Start" to 0.0,
                "Current" to 0.0,
                "Change" to 0.0,
                "Average" to 0.0
            )
        }

        val startWeight = records.lastOrNull()?.weight?.toDoubleOrNull() ?: 0.0
        val currentWeight = records.firstOrNull()?.weight?.toDoubleOrNull() ?: 0.0
        val changeWeight = currentWeight - startWeight
        val averageWeight = records.mapNotNull { it.weight.toDoubleOrNull() }.average()

        return mapOf(
            "Start" to startWeight,
            "Current" to currentWeight,
            "Change" to changeWeight,
            "Average" to averageWeight
        )
    }

    private fun convertToKiloUnit(records: MutableList<UserWeightBean>) {
        for (i in records.indices) {
            records[i].weight = (records[i].weight.toDouble() * 2.2046).toString()
        }
    }
}