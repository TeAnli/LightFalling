package top.teanli.lightfalling.module.modules.display

import top.teanli.lightfalling.event.impl.Render2DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.RenderTool
import top.teanli.lightfalling.tool.TickRateTool
import top.teanli.lightfalling.tool.I18n
import java.awt.Color

object HUD : Module("HUD", "Displays player status information", ModuleCategory.DISPLAY) {

    private val showFlying = checkbox("flying", true)
    private val showSneaking = checkbox("sneaking", true)
    private val showSprinting = checkbox("sprinting", true)
    private val showSwimming = checkbox("swimming", true)
    private val showCrawling = checkbox("crawling", true)
    private val showBurning = checkbox("burning", true)
    private val showCoords = checkbox("coords", true)
    private val showFacing = checkbox("facing", true)
    private val showFPS = checkbox("fps", true)
    private val showTPS = checkbox("tps", true)
    private val showMSPT = checkbox("mspt", true)
    
    private val shadow = checkbox("shadow", true)
    private val posX = slider("posx", 10.0, 0.0, 1000.0, 0)
    private val posY = slider("posy", 10.0, 0.0, 1000.0, 0)

    private val onRender2D = listen<Render2DEvent> { event ->
        val player = mc.player ?: return@listen
        val guiGraphics = event.guiGraphics

        val statusList = mutableListOf<Pair<String, Color>>()

        // 1. Check Flying
        if (showFlying.value && player.abilities.flying) {
            statusList.add(I18n.translate("lightfalling.module.hud.flying") to Color.CYAN)
        }

        // 2. Check Sneaking
        if (showSneaking.value && player.isMovingSlowly) {
            statusList.add(I18n.translate("lightfalling.module.hud.sneaking") to Color.LIGHT_GRAY)
        }

        // 3. Check Sprinting
        if (showSprinting.value && player.isSprinting) {
            statusList.add(I18n.translate("lightfalling.module.hud.sprinting") to Color.GREEN)
        }

        // 4. Check Swimming
        if (showSwimming.value && player.isSwimming) {
            statusList.add(I18n.translate("lightfalling.module.hud.swimming") to Color.BLUE)
        }

        // 5. Check Crawling
        if (showCrawling.value && player.isVisuallyCrawling) {
            statusList.add(I18n.translate("lightfalling.module.hud.crawling") to Color.ORANGE)
        }

        // 6. Check Burning
        if (showBurning.value && player.isOnFire) {
            statusList.add(I18n.translate("lightfalling.module.hud.burning") to Color.RED)
        }

        // 7. Utility Information (Coords, Facing, FPS)
        if (showCoords.value) {
            val pos = player.onPos
            statusList.add("${I18n.translate("lightfalling.module.hud.coords")}: ${pos.x}, ${pos.y}, ${pos.z}" to Color.WHITE)
        }

        if (showFacing.value) {
            statusList.add("${I18n.translate("lightfalling.module.hud.facing")}: ${player.direction.name.uppercase()}" to Color.WHITE)
        }

        if (showFPS.value) {
            statusList.add("${I18n.translate("lightfalling.module.hud.fps")}: ${mc.fps}" to Color.WHITE)
        }

        if (showTPS.value) {
            val tps = TickRateTool.tps
            val color = when {
                tps > 18.0f -> Color.GREEN
                tps > 15.0f -> Color.YELLOW
                else -> Color.RED
            }
            statusList.add("${I18n.translate("lightfalling.module.hud.tps")}: ${String.format("%.1f", tps)}" to color)
        }

        if (showMSPT.value) {
            val mspt = TickRateTool.getMspt()
            val color = when {
                mspt < 35.0f -> Color.GREEN
                mspt < 50.0f -> Color.YELLOW
                else -> Color.RED
            }
            statusList.add("${I18n.translate("lightfalling.module.hud.mspt")}: ${String.format("%.1f", mspt)}" to color)
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
