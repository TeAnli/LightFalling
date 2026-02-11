package top.teanli.lightfalling.module.modules.player

import net.minecraft.client.Minecraft
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object CustomFOV : Module("CustomFOV", "Customize your field of view", ModuleCategory.PLAYER) {
    val fov = slider("fov", 110.0, 30.0, 160.0, 0)

    override fun onEnable() {
        val mc = Minecraft.getInstance()
        if (mc.options != null) {
            // Initialize custom FOV with current vanilla value
            fov.value = mc.options.fov().get().toDouble()
        }
    }

    override fun onDisable() {
        val mc = Minecraft.getInstance()
        if (mc.options != null) {
            // Apply custom FOV value to vanilla on disable
            mc.options.fov().set(fov.value.toInt())
        }
    }
}