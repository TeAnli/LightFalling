package top.teanli.lightfalling.module.modules.combat


import net.minecraft.item.AxeItem
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.accessor.IMinecraftClient
import java.util.Random

class AutoClicker : Module("AutoClicker", "Automatically clicks for you", ModuleCategory.COMBAT) {

    private val minCps = slider("MinCPS", 8.0, 1.0, 20.0, 1)
    private val maxCps = slider("MaxCPS", 12.0, 1.0, 20.0, 1)
    private val jitter = slider("Jitter", 0.0, 0.0, 4.0, 1)
    private val onlyWeapon = checkbox("OnlyWeapon", false)
    private val leftClick = checkbox("LeftClick", true)
    private val rightClick = checkbox("RightClick", false)
    private val holdToClick = checkbox("HoldToClick", true)
    private val dropClickChance = slider("DropChance", 0.05, 0.0, 0.2, 2)

    private val random = Random()
    private var nextLeftClick = 0L
    private var nextRightClick = 0L
    private var lastCpsUpdate = 0L
    private var currentTargetCps = 10.0

    init {
        listen<TickEvent> {
            if (mc.currentScreen != null) return@listen
            val player = mc.player ?: return@listen

            // Update target CPS occasionally to simulate human fluctuation
            if (System.currentTimeMillis() - lastCpsUpdate > 1000 + random.nextInt(2000)) {
                currentTargetCps = minCps.value + random.nextDouble() * (maxCps.value - minCps.value)
                lastCpsUpdate = System.currentTimeMillis()
            }

            // Left Click
            if (leftClick.value) {
                if (!holdToClick.value || mc.options.attackKey.isPressed) {
                    if (onlyWeapon.value) {
                        val item = player.mainHandStack.item
                        if (item !is AxeItem) return@listen
                    }

                    if (System.currentTimeMillis() >= nextLeftClick) {
                        if (random.nextDouble() > dropClickChance.value) {
                            clickLeft()
                        }
                        generateNextLeftDelay()
                    }
                }
            }

            // Right Click
            if (rightClick.value) {
                if (!holdToClick.value || mc.options.useKey.isPressed) {
                    if (System.currentTimeMillis() >= nextRightClick) {
                        clickRight()
                        generateNextRightDelay()
                    }
                }
            }
        }
    }

    private fun clickLeft() {
        val imc = mc as IMinecraftClient
        imc.invokeDoAttack()
        
        if (jitter.value > 0) {
            mc.player?.apply {
                yaw += (random.nextFloat() - 0.5f) * jitter.value.toFloat() * 0.1f
                pitch += (random.nextFloat() - 0.5f) * jitter.value.toFloat() * 0.1f
            }
        }
    }

    private fun clickRight() {
        val imc = mc as IMinecraftClient
        imc.invokeDoItemUse()
    }

    private fun generateNextLeftDelay() {
        // Use a distribution that clusters around target CPS but has outliers
        val delay = (1000.0 / currentTargetCps).toLong()
        val variation = (random.nextGaussian() * (delay * 0.2)).toLong()
        nextLeftClick = System.currentTimeMillis() + delay + variation
    }

    private fun generateNextRightDelay() {
        val delay = (1000.0 / currentTargetCps).toLong()
        val variation = (random.nextGaussian() * (delay * 0.1)).toLong()
        nextRightClick = System.currentTimeMillis() + delay + variation
    }

    override fun onEnable() {
        nextLeftClick = 0L
        nextRightClick = 0L
    }
}
