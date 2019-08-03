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
            0 -> "Sunday"
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            else -> throw IllegalArgumentException("Index does not map to a day")
        }
    }

    fun getFirstDateWithEvents(today: DateTime, dataMap: TreeMap<Long, MutableList<ScheduledEvent>>): Long? {
        return if(dataMap.containsKey(today.millis)) {
            today.millis
        }else{
            dataMap.ceilingKey(DateTime().withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0).millis)
        }

    }
}