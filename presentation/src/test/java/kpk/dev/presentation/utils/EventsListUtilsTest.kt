package kpk.dev.presentation.utils

import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class EventsListUtilsTest {

    var eventsListUtils: EventsListUtils? = null
    var dateUIUtils: DateUIUtils? = null

    @Before
    fun setUp() {
        dateUIUtils = DateUIUtils()
        eventsListUtils = EventsListUtils(dateUIUtils!!)
    }

    @Test
    fun `when trying to find today 08_03_2019 since today does not have events return index 1 (08_05_2019) `() {
        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
        val result = eventsListUtils?.getPositionOfNearestGreaterOrEqualDayWithEvents(thirdAugust, generateEventsMapWithoutToday())
        assertEquals(1, result)
    }

    @Test
    fun `when trying to find today 08_03_2019 since today has events return index 0 (08_03_2019) `() {
        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
        val result = eventsListUtils?.getPositionOfNearestGreaterOrEqualDayWithEvents(thirdAugust, generateEventsMapWithTodayKey())
        assertEquals(0, result)
    }

    @Test
    fun `when trying to find today 08_03_2019 and the map contains only older dates return index 0 `() {
        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
        val result = eventsListUtils?.getPositionOfNearestGreaterOrEqualDayWithEvents(thirdAugust, generateEventsMapWithOlderDates())
        assertEquals(0, result)
    }

    @Test
    fun `when trying to find today 08_03_2019 and the map is empty return index 0 `() {
        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
        val result = eventsListUtils?.getPositionOfNearestGreaterOrEqualDayWithEvents(thirdAugust, generateEventsMapWithOlderDates())
        assertEquals(0, result)
    }

    @After
    fun tearDown() {
        dateUIUtils = null
        eventsListUtils = null
    }

    private fun generateEventsMapWithoutToday(): TreeMap<Long, MutableList<ScheduledEvent>> {
        val fakeData = TreeMap<Long, MutableList<ScheduledEvent>>()
        fakeData.put(1564693200000, mutableListOf()) //older date - 08/02/2019
        fakeData.put(1564952400000, mutableListOf())// searching for this key - 08/05/2019
        fakeData.put(1565038800000, mutableListOf())// 08/06/2019

        return fakeData
    }

    private fun generateEventsMapWithTodayKey(): TreeMap<Long, MutableList<ScheduledEvent>> {
        val fakeData = TreeMap<Long, MutableList<ScheduledEvent>>()
        fakeData.put(1564779600000, mutableListOf())//today 08/03/2019
        fakeData.put(1564866000000, mutableListOf())
        return fakeData
    }

    private fun generateEventsMapWithOlderDates(): TreeMap<Long, MutableList<ScheduledEvent>> {
        val fakeData = TreeMap<Long, MutableList<ScheduledEvent>>()
        fakeData.put(1564606800000, mutableListOf())
        fakeData.put(1564693200000, mutableListOf())
        return fakeData
    }

    private fun generateEmptyMap(): TreeMap<Long, MutableList<ScheduledEvent>> = TreeMap()
}