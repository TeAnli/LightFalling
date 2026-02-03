package top.teanli.lightfalling.event.impl

import top.teanli.lightfalling.event.Event

/**
 * Event posted every client tick.
 */
class TickEvent : Event()

/**
 * Event posted before and after player motion updates.
 */
class MotionEvent(val stage: Stage) : Event() {
    enum class Stage {
        PRE, POST
    }
}
