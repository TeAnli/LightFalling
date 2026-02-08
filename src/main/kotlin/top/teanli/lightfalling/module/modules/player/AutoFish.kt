package top.teanli.lightfalling.module.modules.player

import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.FishingRodItem
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
        if (packet is ClientboundSoundPacket) {
            val soundId = packet.sound.value().location
            if (soundId == SoundEvents.FISHING_BOBBER_SPLASH.location) {
                val player = mc.player ?: return@listen
                val fishHook = player.fishing ?: return@listen

                val dist = fishHook.distanceToSqr(packet.x, packet.y, packet.z)
                if (dist <= 4.0) {
                    startReelIn()
                }
            }
        }
    }

    private val onTick = listen<TickEvent> {
        val player = mc.player ?: return@listen
        
        if (player.mainHandItem.item !is FishingRodItem && player.offhandItem.item !is FishingRodItem) {
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
        } else if (reelState == State.NONE && player.fishing == null) {
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
        val hand = if (player.mainHandItem.item is FishingRodItem) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND
        
        mc.gameMode?.useItem(player, hand)
        player.swing(hand)
        
        reelState = State.NONE
        tickCounter = getDelayedTicks(castDelay.value.toInt())
    }

    private fun startCast() {
        if (reelState != State.NONE) return
        reelState = State.CASTING
        tickCounter = getDelayedTicks(castDelay.value.toInt())
    }

    private fun executeCast() {
        val player = mc.player ?: return
        val hand = if (player.mainHandItem.item is FishingRodItem) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND
        
        mc.gameMode?.useItem(player, hand)
        player.swing(hand)
        
        reelState = State.NONE
    }

    private fun getDelayedTicks(base: Int): Int {
        val v = variation.value.toInt()
        return base + if (v > 0) (0..v).random() else 0
    }

    override fun onEnable() {
        reelState = State.NONE
        tickCounter = -1
    }
}
