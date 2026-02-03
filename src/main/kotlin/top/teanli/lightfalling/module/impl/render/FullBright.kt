package top.teanli.lightfalling.module.impl.render

import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class FullBright : Module("FullBright", "Makes everything bright", ModuleCategory.DISPLAY) {
    private var oldGamma: Double = 1.0

    override fun onEnable() {
        oldGamma = mc.options.gamma.value
        mc.options.gamma.value = 100.0
    }

    override fun onDisable() {
        mc.options.gamma.value = oldGamma
    }
}
