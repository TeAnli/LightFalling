package top.teanli.lightfalling.event.impl

import net.minecraft.network.packet.Packet
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

/**
 * Event posted when a packet is sent or received.
 */
open class PacketEvent(val packet: Packet<*>) : Event() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}
