package top.teanli.lightfalling.module.modules.movement

import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class Sprint : Module("Sprint", "Automatically sprints for you", ModuleCategory.MOVEMENT) {
    
    val tickEvent = listen<TickEvent> {
        if (mc.player != null && mc.player!!.forwardSpeed > 0 && !mc.player!!.isSneaking && !mc.player!!.horizontalCollision) {
            mc.player!!.isSprinting = true
        }
    }

    override fun onDisable() {
        mc.player?.isSprinting = false
    }
}
