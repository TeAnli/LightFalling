package top.teanli.lightfalling.module.modules.player

import net.minecraft.world.phys.Vec3
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

class AntiAFK : Module("AntiAFK", "Prevents you from being kicked for being AFK", ModuleCategory.PLAYER) {

    private val mode = mode("Mode", "Rotate", listOf("Rotate", "Jump", "Move"))
    private val delay = slider("Delay", 5.0, 1.0, 60.0, 1) // Seconds
    
    private val random = Random()
    private var lastActionTime = 0L

    val tickEvent = listen<TickEvent> {
        val player = mc.player ?: return@listen

        if (System.currentTimeMillis() - lastActionTime < delay.value * 1000) {
            return@listen
        }

        when (mode.value) {
            "Rotate" -> {
                player.yRot += (random.nextFloat() - 0.5f) * 45f
                player.xRot = (random.nextFloat() - 0.5f) * 20f
            }
            "Jump" -> {
                if (player.onGround()) {
                    player.jumpFromGround()
                }
            }
            "Move" -> {
                val yawRad = Math.toRadians(player.yRot.toDouble())
                val x = -sin(yawRad) * 0.1
                val z = cos(yawRad) * 0.1
                player.deltaMovement = Vec3(x, player.deltaMovement.y, z)
            }
        }

        lastActionTime = System.currentTimeMillis()
    }

    override fun onEnable() {
        lastActionTime = System.currentTimeMillis()
    }
}
