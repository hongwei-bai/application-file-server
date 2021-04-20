package com.hongwei.util

import com.hongwei.constants.Constants.TimeUnit.MILLIS_PER_MONTH
import java.util.*

object DateUtil {
    fun getFirstDayOfNextMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis + MILLIS_PER_MONTH
    }
}