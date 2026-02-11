package top.teanli.lightfalling.module.modules.player

import net.minecraft.core.BlockPos
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class DeathPoint : Module(
    "DeathPoint",
    "Records your death location and provides teleport functionality",
    ModuleCategory.PLAYER
) {
    companion object {
        var lastDeathPos: BlockPos? = null
    }

    private var wasAlive = true

    private val onTick = listen<TickEvent> {
        val player = mc.player ?: return@listen
        
        val isAlive = player.isAlive
        if (wasAlive && !isAlive) {
            // Player just died
            lastDeathPos = player.blockPosition()
        }
        wasAlive = isAlive
    }
}
