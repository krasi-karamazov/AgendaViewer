package kpk.dev.domain.usecases.events

import io.reactivex.Observable
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.model.repository.events.EventsRepository
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(val repo: EventsRepository): IGetEventsUseCase {
    override fun getEvents(fromDay: DateTime, toDay: DateTime): Observable<TreeMap<Long, MutableList<ScheduledEvent>>>{
        return repo.getEvents(fromDay, toDay)
    }
}