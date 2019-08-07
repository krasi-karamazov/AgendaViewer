# AgendaViewer
A simple agenda viewer which shows the events in all the device's calendars. The implemented requirements are marked as Done.
Below the Requirements are listed some TODOs that given time I would like to work on

### Requirements
(App) targetSdkVersion must be 26

∙ (App) Must handle runtime permissions request to access the native calendar provider
##### Done
∙ (Agenda List) Must be able to handle multiple calendars on the device. Very important.
##### Done
∙ (Availability checker) Must find an open slot at the next 30 minute increment from the current time. If current time is 5:33 then the soonest available slot you should find is 6:00
##### Done
∙ (Availability checker) Only events with an availability of Busy should be considered as time slots that you aren’t available for
##### Done
∙ (Agenda List) All day events should show the start/end time as “ALL DAY”
##### Done
∙ (Agenda List) Show the calendar color somewhere on the event cell so the user can identify which calendar the event belongs to
##### Done
∙ (Agenda List) You should create sticky headers to group the events by day
##### Done
 *Bonus points:* 

∙ Allow for user to choose between 30 minutes or 1 hour time slot for availability check. So instead of finding an open 30 minute window you would be finding an open 1 hour window where there are no other events.
##### Done


### TODO

∙ Work a bit on readability on some portions of the code

∙ Implement extensive unit testing. Right now, because of time constraints, the unit tests are limited to just being a showcase of how I usually do them

∙ Spend more time manually testing

∙ Split up CalendarContentResolver in multiple classes (commands which call the central content resolver)

∙ Work a bit on UI and UX as currently it's not optimal

∙ Work on optimizations (memory and time)
