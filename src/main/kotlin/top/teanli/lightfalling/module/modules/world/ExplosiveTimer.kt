package top.teanli.lightfalling.module.modules.world

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.world.entity.monster.Creeper
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.impl.TNTRenderEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.ColorTool
import top.teanli.lightfalling.tool.EntityTool
import top.teanli.lightfalling.tool.Render3DTool
import top.teanli.lightfalling.tool.RenderTool

class ExplosiveTimer : Module(
    "ExplosiveTimer",
    "Shows explosion timer for TNT and creepers",
    ModuleCategory.WORLD
) {
    private val showTNT = checkbox("showtnt", true)
    private val showCreeper = checkbox("showcreeper", true)
    private val scanRange = slider("scanrange", 64.0, 16.0, 128.0, 0)

    val tntRenderEvent = listen<TNTRenderEvent> { event ->
        if (event.tntEntityRenderState.fuseRemainingInTicks <= 0 || !showTNT.value) {
            return@listen
        }
        val timeLeft: Float = event.tntEntityRenderState.fuseRemainingInTicks / 20.0f
        val text = String.format("%.1f", timeLeft)

        val font = mc.font

        event.poseStack.pushPose()

        event.poseStack.translate(0.5, 1.7, 0.5)

        val camera = mc.gameRenderer.mainCamera
        if (camera != null) {
            val rotation = camera.rotation()
            event.poseStack.mulPose(rotation)
        }

        val scale = 0.025f
        event.poseStack.scale(scale, -scale, scale)

        val textWidth = RenderTool.getTextWidth(text)
        val xOffset = -textWidth / 2.0f

        val color = ColorTool.getTimeColor(timeLeft)

        font.drawInBatch(
            text,
            xOffset,
            0.0f,
            color.rgb,
            true,
            event.poseStack.last().pose(),
            mc.renderBuffers().bufferSource(),
            Font.DisplayMode.NORMAL,
            0x60000000,
            LightTexture.FULL_BRIGHT,
        )

        event.poseStack.popPose()
    }
    private val onRender3D = listen<Render3DEvent> { event ->
        if (!showCreeper.value) return@listen
        
        val player = mc.player ?: return@listen
        val range = scanRange.value
        val playerPos = player.position()
        
        // Scan for creepers within range
        val entities = EntityTool.getEntitiesInRange(playerPos, range)
        for (entity in entities) {
            if (entity is Creeper) {
                val swellProgress = entity.getSwelling(mc.deltaTracker.getGameTimeDeltaPartialTick(false))
                if (swellProgress > 0) {
                    val timeLeft = (1.0f - swellProgress) * 1.5f
                    if (timeLeft > 0) {
                        val text = String.format("%.1fs", timeLeft)
                        val color = ColorTool.getTimeColor(timeLeft)
                        
                        Render3DTool.renderText3D(
                            event.poseStack,
                            event.buffer,
                            event.camera.position(),
                            entity.position().x,
                            entity.position().y + entity.bbHeight + 0.5,
                            entity.position().z,
                            text,
                            color
                        )
                    }
                }
            }
        }
    }
}
