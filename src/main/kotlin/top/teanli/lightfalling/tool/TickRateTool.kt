package top.teanli.lightfalling.tool

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import top.teanli.lightfalling.event.EventManager
import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.event.impl.PacketEvent
import top.teanli.lightfalling.event.listen
import kotlin.math.max
import kotlin.math.min

object TickRateTool : EventListener {
    override val eventHandlers = mutableListOf<EventListener.EventHandler<out top.teanli.lightfalling.event.Event>>()
    
    private var lastPacketTime = -1L
    private val tickRates = FloatArray(20)
    private var nextIndex = 0
    
    var tps = 20.0f
        private set

    private val onPacket = listen<PacketEvent.Receive> { event ->
        if (event.packet is WorldTimeUpdateS2CPacket) {
            val now = System.currentTimeMillis()
            if (lastPacketTime != -1L) {
                val timeElapsed = (now - lastPacketTime) / 1000.0f
                val currentTps = 20.0f / timeElapsed
                
                tickRates[nextIndex] = min(20.0f, currentTps)
                nextIndex = (nextIndex + 1) % tickRates.size
                
                var total = 0.0f
                for (rate in tickRates) {
                    total += rate
                }
                tps = total / tickRates.size
            }
            lastPacketTime = now
        }
    }

    init {
        for (i in tickRates.indices) {
            tickRates[i] = 20.0f
        }
    }
    
    fun getMspt(): Float {
        return if (tps <= 0) 0f else 1000.0f / tps
    }
}
