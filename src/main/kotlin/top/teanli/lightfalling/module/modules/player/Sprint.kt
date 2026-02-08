package top.teanli.lightfalling.module.modules.player

import top.teanli.lightfalling.event.impl.MotionEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class Sprint : Module("Sprint", "Automatically sprints for you", ModuleCategory.PLAYER) {

    val motionEvent = listen<MotionEvent> {
        if (it.stage == MotionEvent.Stage.PRE) {
            val player = mc.player ?: return@listen

            if (!player.isMovingSlowly &&
                !player.horizontalCollision &&
                player.foodData.foodLevel > 6 &&
                !player.isUsingItem) {
                player.isSprinting = true
            }
        }
    }

    override fun onDisable() {
        mc.player?.isSprinting = false
    }
}