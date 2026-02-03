package top.teanli.lightfalling.module.modules.render

import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object BrightnessChanger : Module("BrightnessChanger", "Modifies the game brightness (Gamma)", ModuleCategory.DISPLAY) {
    
    // We will use a Mixin to actually apply this value because Minecraft clamps it between 0 and 1.
    var brightness: Double = 10.0

    override fun onEnable() {
        // The actual logic is handled in MixinSimpleOption or MixinLightmapTextureManager
    }

    override fun onDisable() {
        // Resetting is handled by the Mixin checking the state
    }
}
