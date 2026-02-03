package top.teanli.lightfalling.module.modules.movement

import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class Sprint : Module("Sprint", "Automatically sprints for you", ModuleCategory.PLAYER) {
    override fun onUpdate() {
        if (mc.player != null && mc.player!!.forwardSpeed > 0 && !mc.player!!.isSneaking && !mc.player!!.horizontalCollision) {
            mc.player!!.isSprinting = true
        }
    }

    override fun onDisable() {
        mc.player?.isSprinting = false
    }
}
