package top.teanli.lightfalling.module.modules.player

import net.minecraft.item.FishingRodItem
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import top.teanli.lightfalling.event.impl.PacketEvent
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

@Suppress("UNUSED_PARAMETER")
object AutoFish : Module("AutoFish", "Automatically catches fish for you", ModuleCategory.PLAYER) {

    private val castDelay = slider("Cast Delay", 15.0, 5.0, 50.0, 0)
    private val reactionDelay = slider("Reaction Delay", 0.0, 0.0, 10.0, 0)
    private val variation = slider("Variation", 5.0, 0.0, 20.0, 0)

    private var tickCounter = -1
    private var reelState = State.NONE
    
    private enum class State {
        NONE,
        REELING,
        CASTING
    }

    private val onPacket = listen<PacketEvent.Receive> {
        val packet = it.packet
        if (packet is PlaySoundS2CPacket) {
            val soundId = packet.sound.value().id
            if (soundId == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.id) {
                val player = mc.player ?: return@listen
                val fishHook = player.fishHook ?: return@listen

                val dist = fishHook.squaredDistanceTo(packet.x, packet.y, packet.z)
                if (dist <= 4.0) {
                    startReelIn()
                }
            }
        }
    }

    private val onTick = listen<TickEvent> {
        val player = mc.player ?: return@listen
        
        if (player.mainHandStack.item !is FishingRodItem && player.offHandStack.item !is FishingRodItem) {
            reelState = State.NONE
            tickCounter = -1
            return@listen
        }

        if (tickCounter > 0) {
            tickCounter--
        } else if (tickCounter == 0) {
            tickCounter = -1
            when (reelState) {
                State.REELING -> executeReelIn()
                State.CASTING -> executeCast()
                else -> {}
            }
        } else if (reelState == State.NONE && player.fishHook == null) {
            // Auto cast if not fishing and no action pending
            startCast()
        }
    }

    private fun startReelIn() {
        if (reelState != State.NONE) return
        reelState = State.REELING
        tickCounter = getDelayedTicks(reactionDelay.value.toInt())
    }

    private fun executeReelIn() {
        val player = mc.player ?: return
        val hand = if (player.mainHandStack.item is FishingRodItem) Hand.MAIN_HAND else Hand.OFF_HAND
        
        mc.interactionManager?.interactItem(player, hand)
        player.swingHand(hand)
        
        reelState = State.NONE
        // We don't call startCast() here anymore, the onTick loop will catch it
        // since reelState is now NONE and fishHook will be null soon.
        // We set tickCounter to a small delay to prevent immediate re-casting in the same tick
        tickCounter = getDelayedTicks(castDelay.value.toInt())
    }

    private fun startCast() {
        if (reelState != State.NONE) return
        reelState = State.CASTING
        tickCounter = getDelayedTicks(castDelay.value.toInt())
    }

    private fun executeCast() {
        val player = mc.player ?: return
        val hand = if (player.mainHandStack.item is FishingRodItem) Hand.MAIN_HAND else Hand.OFF_HAND
        
        mc.interactionManager?.interactItem(player, hand)
        player.swingHand(hand)
        
        reelState = State.NONE
    }

    private fun getDelayedTicks(base: Int): Int {
        val varVal = variation.value.toInt()
        val delay = if (varVal <= 0) base else base + (Math.random() * varVal).toInt()
        return delay.coerceAtLeast(1) // Ensure at least 1 tick delay to avoid state overlap
    }

    override fun onEnable() {
        reelState = State.NONE
        tickCounter = -1
    }
}
