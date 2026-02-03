package top.teanli.lightfalling.module.modules.render

import net.minecraft.client.MinecraftClient
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object CustomFOV : Module("CustomFOV", "Customize your field of view", ModuleCategory.WORLD) {
    val fov = slider("FOV", 110.0, 30.0, 160.0, 0)

    override fun onEnable() {
        val mc = MinecraftClient.getInstance()
        if (mc?.options != null) {
            // Initialize custom FOV with current vanilla value
            fov.value = mc.options.fov.value.toDouble()
        }
    }

    override fun onDisable() {
        val mc = MinecraftClient.getInstance()
        if (mc?.options != null) {
            // Apply custom FOV value to vanilla on disable
            mc.options.fov.value = fov.value.toInt()
        }
    }
}
