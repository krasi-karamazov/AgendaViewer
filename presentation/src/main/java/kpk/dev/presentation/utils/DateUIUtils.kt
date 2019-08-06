package kpk.dev.presentation.utils

import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUIUtils @Inject constructor() {

    fun getDayNameByIndex(index: Int): String {
        return when(index) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> throw IllegalArgumentException("Index does not map to a day")
        }
    }

    fun getMonthNameByIndex(index: Int): String {
        return when(index) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> throw IllegalArgumentException("Index does not map to a month")
        }
    }

    fun getFirstDateWithEvents(today: DateTime, dataMap: TreeMap<Long, MutableList<ScheduledEvent>>): Long? {
        return if(dataMap.containsKey(today.millis)) {
            today.millis
        }else{
            dataMap.ceilingKey(today.millis)
        }
    }

    fun getTimeStringForEvent(start: Long, end: Long, isFullDay: Boolean): String {

        val startTime = DateTime(start)
        val endTime = DateTime(end)

        return if(isFullDay) {
            getDayNameByIndex(startTime.dayOfWeek) + ", " + getMonthNameByIndex(startTime.monthOfYear) + " " + startTime.dayOfMonth
        }else{
            getDayNameByIndex(startTime.dayOfWeek) + ", " + getMonthNameByIndex(startTime.monthOfYear) + " " + startTime.toString("HH:mm") + " - " + endTime.toString("HH:mm")
        }
    }
}