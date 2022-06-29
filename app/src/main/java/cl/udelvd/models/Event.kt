package cl.udelvd.models

import java.util.*

data class Event(
    var id: Int = 0,
    var interview: Interview? = null,
    var action: Action? = null,
    var emoticon: Emoticon? = null,
    var justification: String? = null,
    var eventHour: Date? = null
)