package top.teanli.lightfalling.module.modules.player

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object BrightnessChanger : Module("BrightnessChanger", "Modifies the game brightness (Gamma)", ModuleCategory.PLAYER) {

    val mode = mode("mode", "Gamma", listOf("Gamma", "Potion"))
    val brightness = slider("brightness", 10.0, 1.0, 20.0, 1) { mode.value == "Gamma" }

    private val nightVision = listen<TickEvent> {
        val player = mc.player ?: return@listen

        if (mode.value == "Potion") {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false, false))
        } else {
            // If we just switched from Potion to Gamma, remove the effect we added
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                val effect = player.getEffect(MobEffects.NIGHT_VISION)
                // Only remove if it's our "infinite" effect (check duration or ambient)
                if (effect != null && effect.duration > 500 && !effect.isAmbient) {
                    player.removeEffect(MobEffects.NIGHT_VISION)
                }
            }
        }
    }

    override fun onEnable() {
    }

    override fun onDisable() {
        if (mode.value == "Potion") {
            mc.player?.removeEffect(MobEffects.NIGHT_VISION)
        }
    }
}