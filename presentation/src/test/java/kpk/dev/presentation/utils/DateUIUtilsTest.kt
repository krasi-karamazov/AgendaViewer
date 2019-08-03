package kpk.dev.presentation.utils

import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class DateUIUtilsTest {
    private var date: DateTime? = null

    private var dateUIUtils: DateUIUtils? = null
    @Before
    fun setUp() {
        date = DateTime()
        dateUIUtils = DateUIUtils()
    }

    @Test
    fun `when the argument is 0 getDayNameByIndex returns Sunday`() {
        val result = dateUIUtils?.getDayNameByIndex(0)
        assertEquals("Sunday", result)
    }


    @Test
    fun `when the argument is 1 getDayNameByIndex returns Monday`() {
        val result = dateUIUtils?.getDayNameByIndex(1)
        assertEquals("Monday", result)
    }

    @Test
    fun `when the argument is 2 getDayNameByIndex returns Tuesday`() {
        val result = dateUIUtils?.getDayNameByIndex(2)
        assertEquals("Tuesday", result)
    }

    @Test
    fun `when the argument is 3 getDayNameByIndex returns Wednesday`() {
        val result = dateUIUtils?.getDayNameByIndex(3)
        assertEquals("Wednesday", result)
    }

    @Test
    fun `when the argument is 4 getDayNameByIndex returns Thursday`() {
        val result = dateUIUtils?.getDayNameByIndex(4)
        assertEquals("Thursday", result)
    }

    @Test
    fun `when the argument is 5 getDayNameByIndex returns Friday`() {
        val result = dateUIUtils?.getDayNameByIndex(5)
        assertEquals("Friday", result)
    }

    @Test
    fun `when the argument is 6 getDayNameByIndex returns Saturday`() {
        val result = dateUIUtils?.getDayNameByIndex(6)
        assertEquals("Saturday", result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when the argument is out of bounds (0-6) throw IllegalArgumentException`() {
        dateUIUtils?.getDayNameByIndex(15)
    }

    @Test
    fun `when first day with events is now return today`() {
        val dataMap = generateEventsMapWithTodayKey()

        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)

        assertEquals(thirdAugust.millis, dateUIUtils?.getFirstDateWithEvents(thirdAugust, dataMap))
    }

    @Test
    fun `when first day with events is 2 days from now return 5th august`() {
        val dataMap = generateEventsMapWithoutTodayKey()
        val augustFifth = DateTime().withYear(2019)
                .withDayOfMonth(5)
                .withMonthOfYear(8)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)

        val thirdAugust = DateTime()
                .withYear(2019)
                .withMonthOfYear(8)
                .withDayOfMonth(3)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)

        assertEquals(augustFifth.millis, dateUIUtils?.getFirstDateWithEvents(thirdAugust, dataMap))
    }

    private fun generateEventsMapWithTodayKey(): TreeMap<Long, MutableList<ScheduledEvent>> {
        val fakeData = TreeMap<Long, MutableList<ScheduledEvent>>()
        fakeData.put(1564779600000, mutableListOf())//today 08/03/2019
        fakeData.put(1564866000000, mutableListOf())
        return fakeData
    }

    private fun generateEventsMapWithoutTodayKey(): TreeMap<Long, MutableList<ScheduledEvent>> {
        val fakeData = TreeMap<Long, MutableList<ScheduledEvent>>()
        fakeData.put(1564693200000, mutableListOf()) //older date - 08/02/2019
        fakeData.put(1564952400000, mutableListOf())// searching for this key - 08/05/2019
        fakeData.put(1565038800000, mutableListOf())// 08/06/2019

        return fakeData
    }

    @After
    fun tearDown() {
        date = null
        dateUIUtils = null
    }
}