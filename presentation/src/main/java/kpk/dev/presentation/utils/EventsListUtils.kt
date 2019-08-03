package kpk.dev.presentation.utils

import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsListUtils @Inject constructor(val dateUIUtils: DateUIUtils) {

    fun getPositionOfNearestGreaterOrEqualDayWithEvents(today: DateTime, dataMap: TreeMap<Long, MutableList<ScheduledEvent>>): Int {
        if(dataMap.isEmpty()) {
            return 0
        }

        val firstDateWithEvents = dateUIUtils.getFirstDateWithEvents(today.withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0), dataMap) ?: 0
        val sectionIndex = Arrays.binarySearch(dataMap.keys.toLongArray(), firstDateWithEvents)
        return if(sectionIndex == -1) {
            0
        }else{
            sectionIndex
        }
    }
}