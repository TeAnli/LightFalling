package top.teanli.lightfalling.event

/**
 * Interface for events that can be cancelled.
 */
interface Cancellable {
    var isCancelled: Boolean
}

/**
 * Event priorities.
 * Higher values are processed first.
 */
enum class EventPriority(val value: Int) {
    HIGHEST(200),
    HIGH(100),
    MEDIUM(0),
    LOW(-100),
    LOWEST(-200)
}

/**
 * Base class for all events.
 */
abstract class Event
