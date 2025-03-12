package com.sophisticated.person.under.sun.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.cc.BmiApp
import com.sophisticated.person.under.sun.cc.BmiApp.Companion.sharedPreferences
import com.sophisticated.person.under.sun.data.show.BmiBean
import com.sophisticated.person.under.sun.data.show.BmiRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object BmiUtils {
    fun saveBmiUnit(isLb:Boolean) {
        sharedPreferences.edit().putBoolean("BmiUnit", isLb).apply()
    }
    fun getBmiUnit(): Boolean {
        return sharedPreferences.getBoolean("BmiUnit", false)
    }
    fun getUnitData():String{
        val state = getBmiUnit()
      return  if (!state) {
            "kg"
        } else {
            "lb"
        }
    }
    inline fun <reified T : Activity> Activity.navigateTo(
        finishCurrent: Boolean = false,
        extras: Intent.() -> Unit = {}
    ) {
        val intent = Intent(this, T::class.java)
        intent.extras()
        startActivity(intent)
        if (finishCurrent) {
            finish()
        }
    }

    fun calculateBMIState(heightCmStr: String, weightKgStr: String): String {
        val heightCm = heightCmStr.toDouble()
        val weightKg = weightKgStr.toDouble()
        val heightM = heightCm / 100
        val bmi = weightKg / (heightM * heightM)
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obesity"
        }
    }

    fun calculateBMIState(result: String): Int {
        return when (result) {
            "Underweight" -> 1
            "Normal" -> 2
            "Overweight" -> 3
            "Obesity" -> 4
            else -> 1
        }
    }

    fun calculateBMIColor(result: String): Int {
        return when (result) {
            "Underweight" -> R.color.bmi_light
            "Normal" -> R.color.bmi_normal
            "Overweight" -> R.color.bmi_over
            "Obesity" -> R.color.bmi_obesity
            else -> R.color.bmi_light
        }
    }

    fun calculateBMI(heightCmStr: String, weightKgStr: String): String {
        val heightCm = heightCmStr.toDouble()
        val weightKg = weightKgStr.toDouble()
        val heightM = heightCm / 100
        return String.format(Locale.US, "%.2f", weightKg / (heightM * heightM))
    }

    fun formatTimestamp(currentTimeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val date = Date(currentTimeMillis)
        return sdf.format(date)
    }

    fun formatTimestamp1(currentTimeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val date = Date(currentTimeMillis)
        return sdf.format(date)
    }

    fun formatTimestamp2(currentTimeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(currentTimeMillis)
        return sdf.format(date)
    }

    fun shareText(context: Context) {
        val text = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun showKeyboard(view: View?) {
        if (view == null) return
        view.requestFocus()
        val inputManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View?) {
        if (view == null) return
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun toggleKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
    }

    fun isValidNumber(input: String): Boolean {
        return input.isNotEmpty() && !input.startsWith(".") && input.toDoubleOrNull()
            ?.let { it > 0 } == true
    }
    fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
}