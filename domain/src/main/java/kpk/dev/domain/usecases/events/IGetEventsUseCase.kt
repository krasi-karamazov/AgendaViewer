package kpk.dev.domain.usecases.events

import io.reactivex.Observable
import io.reactivex.Single
import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*

interface IGetEventsUseCase {
    fun getEvents(fromDay: DateTime, toDay: DateTime): Observable<TreeMap<Long, MutableList<ScheduledEvent>>>
}