package top.teanli.lightfalling.event

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Event Manager
 * Responsible for managing event listeners and dispatching events.
 */
object EventManager {
    private val listeners = CopyOnWriteArrayList<EventListener>()

    /**
     * Registers an event listener.
     */
    fun subscribe(listener: EventListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Unregisters an event listener.
     */
    fun unsubscribe(listener: EventListener) {
        listeners.remove(listener)
    }

    /**
     * Posts an event to all registered and active listeners.
     */
    fun <T : Event> post(event: T): T {
        // Collect all handlers for this event type
        val activeHandlers = mutableListOf<Pair<EventListener, EventListener.EventHandler<out Event>>>()
        
        listeners.forEach { listener ->
            if (listener.isEventListenerActive) {
                listener.eventHandlers.forEach { handler ->
                    if (handler.eventClass.isInstance(event)) {
                        activeHandlers.add(listener to handler)
                    }
                }
            }
        }

        // Sort handlers by priority (descending)
        activeHandlers.sortByDescending { it.second.priority.value }

        // Execute handlers
        activeHandlers.forEach { (_, handler) ->
            handler.handle(event)
        }
        
        return event
    }
}
