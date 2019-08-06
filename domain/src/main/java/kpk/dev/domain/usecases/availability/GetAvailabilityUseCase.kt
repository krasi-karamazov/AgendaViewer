package kpk.dev.domain.usecases.availability

import io.reactivex.Observable
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.repository.availability.AvailabilityRepository
import org.joda.time.DateTime
import javax.inject.Inject

class GetAvailabilityUseCase @Inject constructor(val repo: AvailabilityRepository): IGetAvailabilityUseCase {
    override fun getAvailableSlotForMeeting(duration: CalendarContentResolver.EventDuration): Observable<DateTime> = repo.getAvailibility(duration)
}