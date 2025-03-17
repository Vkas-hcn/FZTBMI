package com.ling.ding.sighed.limt

import com.ling.ding.sighed.txtdata.LocalStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeManager(
    private val localStorage: LocalStorage,
    private val rules: List<AdLimitRule>
) {
    private val currentDateFormatter = DateTimeFormatter.BASIC_ISO_DATE
    private val currentTimeFormatter = DateTimeFormatter.ofPattern("HH")

    private var lastCheckedDate: String = getCurrentDate()
    private var lastCheckedHour: String = getCurrentHour()

    fun updateTime() {
        val currentDate = getCurrentDate()
        val currentHour = getCurrentHour()

        if (lastCheckedDate != currentDate) {
            rules.forEach { it.reset() }
            lastCheckedDate = currentDate
        }

        if (lastCheckedHour != currentHour) {
            rules.filterIsInstance<HourlyShowLimitRule>().forEach { it.reset() }
            lastCheckedHour = currentHour
        }
    }

    private fun getCurrentDate(): String {
        return LocalDate.now().format(currentDateFormatter)
    }

    private fun getCurrentHour(): String {
        return LocalTime.now().format(currentTimeFormatter)
    }
}
