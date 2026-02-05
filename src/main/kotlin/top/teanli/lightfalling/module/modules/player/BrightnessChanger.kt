package top.teanli.lightfalling.module.modules.player

import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object BrightnessChanger : Module("BrightnessChanger", "Modifies the game brightness (Gamma)", ModuleCategory.PLAYER) {

    val mode = mode("Mode", "Gamma", listOf("Gamma", "Potion"))
    val brightness = slider("Brightness", 10.0, 1.0, 20.0, 1) { mode.value == "Gamma" }

    private val nightVision = listen<TickEvent> {
        val player = mc.player ?: return@listen

        if (mode.value == "Potion") {
            player.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000, 0, false, false, false))
        } else {
            // If we just switched from Potion to Gamma, remove the effect we added
            if (player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                val effect = player.getStatusEffect(StatusEffects.NIGHT_VISION)
                // Only remove if it's our "infinite" effect (check duration or ambient)
                if (effect != null && effect.duration > 500 && !effect.isAmbient) {
                    player.removeStatusEffect(StatusEffects.NIGHT_VISION)
                }
            }
        }
    }

    override fun onEnable() {
    }

    override fun onDisable() {
        if (mode.value == "Potion") {
            mc.player?.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
    }
}