package top.teanli.lightfalling.module.modules.display

import top.teanli.lightfalling.event.impl.Render2DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.RenderTool
import top.teanli.lightfalling.tool.TickRateTool
import java.awt.Color

object HUD : Module("HUD", "Displays player status information", ModuleCategory.DISPLAY) {

    private val showFlying = checkbox("Flying", true)
    private val showSneaking = checkbox("Sneaking", true)
    private val showSprinting = checkbox("Sprinting", true)
    private val showSwimming = checkbox("Swimming", true)
    private val showCrawling = checkbox("Crawling", true)
    private val showBurning = checkbox("Burning", true)
    private val showCoords = checkbox("Coords", true)
    private val showFacing = checkbox("Facing", true)
    private val showFPS = checkbox("FPS", true)
    private val showTPS = checkbox("TPS", true)
    private val showMSPT = checkbox("MSPT", true)
    
    private val shadow = checkbox("Shadow", true)
    private val posX = slider("PosX", 10.0, 0.0, 1000.0, 0)
    private val posY = slider("PosY", 10.0, 0.0, 1000.0, 0)

    private val onRender2D = listen<Render2DEvent> { event ->
        val player = mc.player ?: return@listen
        val guiGraphics = event.guiGraphics

        val statusList = mutableListOf<Pair<String, Color>>()

        // 1. Check Flying
        if (showFlying.value && player.abilities.flying) {
            statusList.add("Flying" to Color.CYAN)
        }

        // 2. Check Sneaking
        if (showSneaking.value && player.isMovingSlowly) {
            statusList.add("Sneaking" to Color.LIGHT_GRAY)
        }

        // 3. Check Sprinting
        if (showSprinting.value && player.isSprinting) {
            statusList.add("Sprinting" to Color.GREEN)
        }

        // 4. Check Swimming
        if (showSwimming.value && player.isSwimming) {
            statusList.add("Swimming" to Color.BLUE)
        }

        // 5. Check Crawling
        if (showCrawling.value && player.isVisuallyCrawling) {
            statusList.add("Crawling" to Color.ORANGE)
        }

        // 6. Check Burning
        if (showBurning.value && player.isOnFire) {
            statusList.add("Burning" to Color.RED)
        }

        // 7. Utility Information (Coords, Facing, FPS)
        if (showCoords.value) {
            val pos = player.onPos
            statusList.add("XYZ: ${pos.x}, ${pos.y}, ${pos.z}" to Color.WHITE)
        }

        if (showFacing.value) {
            statusList.add("Facing: ${player.direction.name.uppercase()}" to Color.WHITE)
        }

        if (showFPS.value) {
            statusList.add("FPS: ${mc.fps}" to Color.WHITE)
        }

        if (showTPS.value) {
            val tps = TickRateTool.tps
            val color = when {
                tps > 18.0f -> Color.GREEN
                tps > 15.0f -> Color.YELLOW
                else -> Color.RED
            }
            statusList.add("TPS: ${String.format("%.1f", tps)}" to color)
        }

        if (showMSPT.value) {
            val mspt = TickRateTool.getMspt()
            val color = when {
                mspt < 35.0f -> Color.GREEN
                mspt < 50.0f -> Color.YELLOW
                else -> Color.RED
            }
            statusList.add("MSPT: ${String.format("%.1f", mspt)}" to color)
        }

        // Render the status list
        var currentY = posY.value.toInt()
        for ((status, color) in statusList) {
            RenderTool.drawText(
                guiGraphics,
                status,
                posX.value.toInt(),
                currentY,
                color.rgb,
                shadow.value
            )
            currentY += RenderTool.getFontHeight() + 2
        }
    }
}
