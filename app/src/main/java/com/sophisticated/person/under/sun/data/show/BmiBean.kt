package com.sophisticated.person.under.sun.data.show

import android.content.Context
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Keep
data class BmiBean (
    var weight: String = "0",
    var height: String = "0",
    var remark: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
@Keep
data class UserWeightBean (
    var weight: String = "0",
    var remark: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
class BmiRepository(private val context: Context) {
    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences("BmiRecords", Context.MODE_PRIVATE)

    // Convert list of BmiBean to JSON string and save to SharedPreferences
    private fun saveBmiRecords(bmiList: MutableList<BmiBean>) {
        val jsonString = gson.toJson(bmiList)
        sharedPreferences.edit().putString("BmiRecords", jsonString).apply()
    }

    // Retrieve JSON string from SharedPreferences and convert it to list of BmiBean
    private fun getBmiRecords(): MutableList<BmiBean> {
        val jsonString = sharedPreferences.getString("BmiRecords", null)
        return if (jsonString != null) {
            val listType = object : TypeToken<MutableList<BmiBean>>() {}.type
            gson.fromJson(jsonString, listType)
        } else {
            mutableListOf()
        }
    }

    // Add a new BmiBean to the list and save
    fun addRecord(bmiBean: BmiBean) {
        val bmiList = getBmiRecords()
        bmiList.add(bmiBean)
        saveBmiRecords(bmiList)
    }

//------------------------------------Weight-----------------------------------
    fun getAllWeightRecords(): MutableList<UserWeightBean> {
        val bmiList = getWeightRecords()
        return (bmiList.sortedByDescending { it.timestamp }).toMutableList()
    }
    private fun getWeightRecords(): MutableList<UserWeightBean> {
        val jsonString = sharedPreferences.getString("WeightRecords", null)
        return if (jsonString != null) {
            val listType = object : TypeToken<MutableList<UserWeightBean>>() {}.type
            gson.fromJson(jsonString, listType)
        } else {
            mutableListOf()
        }
    }

    // Add a new BmiBean to the list and save
    fun addWeightRecord(userWeightBean: UserWeightBean) {
        val weightList = getWeightRecords()
        weightList.add(userWeightBean)
        saveWeightRecords(weightList)
    }
    // edit weight
    fun editWeightRecord(userWeightBean: UserWeightBean) {
        val weightList = getWeightRecords()
        weightList.removeAll { it.timestamp == userWeightBean.timestamp }
        weightList.add(userWeightBean)
        saveWeightRecords(weightList)
    }

    // Delete a BmiBean from the list by timestamp and save
    fun deleteWeightRecord(timestamp: Long) {
        val weightList = getWeightRecords()
        weightList.removeAll { it.timestamp == timestamp }
        saveWeightRecords(weightList)
    }
    // Convert list of BmiBean to JSON string and save to SharedPreferences
    private fun saveWeightRecords(weightList: MutableList<UserWeightBean>) {
        val jsonString = gson.toJson(weightList)
        sharedPreferences.edit().putString("WeightRecords", jsonString).apply()
    }
}