package top.teanli.lightfalling.module.modules.player

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import top.teanli.lightfalling.event.impl.PacketEvent
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object AutoFish : Module("AutoFish", "Automatically catches fish for you", ModuleCategory.PLAYER) {

    private val castDelay = slider("Cast Delay", 15.0, 5.0, 50.0, 0)
    private var tickCounter = 0
    private var shouldCast = false

    private val onPacket = listen<PacketEvent.Receive> {
        val packet = it.packet
        if (packet is PlaySoundS2CPacket) {
            // Check if the sound is the fishing bobber splash
            if (packet.sound.value().id.path == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.id.path) {
                val player = mc.player ?: return@listen
                val fishObject = player.fishHook ?: return@listen
                
                // Check if the sound is close to our bobber
                val dist = fishObject.squaredDistanceTo(packet.x, packet.y, packet.z)
                if (dist <= 4.0) {
                    reelIn()
                }
            }
        }
    }

    private val onTick = listen<TickEvent> {
        if (shouldCast) {
            if (tickCounter > 0) {
                tickCounter--
            } else {
                cast()
                shouldCast = false
            }
        }
    }

    private fun reelIn() {
        // Right click to reel in
        mc.interactionManager?.interactItem(mc.player, Hand.MAIN_HAND)
        shouldCast = true
        tickCounter = castDelay.value.toInt()
    }

    private fun cast() {
        // Right click to cast
        mc.interactionManager?.interactItem(mc.player, Hand.MAIN_HAND)
    }

    override fun onEnable() {
        tickCounter = 0
        shouldCast = false
    }
}
