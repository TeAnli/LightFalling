package top.teanli.lightfalling

import net.fabricmc.api.ModInitializer
import top.teanli.lightfalling.module.ModuleManager

class Lightfalling : ModInitializer {

    override fun onInitialize() {
        ModuleManager.init()
    }
}
