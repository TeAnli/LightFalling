package top.teanli.lightfalling.event

/**
 * Interface for event listeners providing functional event handling.
 */
interface EventListener {
    // List of functional event handlers
    val eventHandlers: MutableList<EventHandler<out Event>>

    /**
     * Whether the listener is active and should receive events.
     * Defaults to true.
     */
    val isEventListenerActive: Boolean get() = true

    /**
     * Registers a functional event listener.
     */
    fun <T : Event> listen(
        eventClass: Class<T>,
        priority: EventPriority = EventPriority.MEDIUM,
        action: (T) -> Unit
    ): EventHandler<T> {
        val handler = EventHandler(eventClass, priority, action)
        eventHandlers.add(handler)
        return handler
    }

    /**
     * Data class representing a functional event handler.
     */
    class EventHandler<T : Event>(
        val eventClass: Class<T>,
        val priority: EventPriority,
        val action: (T) -> Unit
    ) {
        fun handle(event: Event) {
            if (eventClass.isInstance(event)) {
                @Suppress("UNCHECKED_CAST")
                action(event as T)
            }
        }
    }
}

/**
 * Inline helper for listen function to support reified types.
 */
inline fun <reified T : Event> EventListener.listen(
    priority: EventPriority = EventPriority.MEDIUM,
    noinline action: (T) -> Unit
): EventListener.EventHandler<T> {
    return listen(T::class.java, priority, action)
}
