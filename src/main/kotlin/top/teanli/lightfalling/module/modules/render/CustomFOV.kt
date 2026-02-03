package top.teanli.lightfalling.module.modules.render

import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

object CustomFOV : Module("CustomFOV", "Customize your field of view", ModuleCategory.WORLD) {
    val fov = slider("FOV", 110.0, 30.0, 160.0, 0)
}
