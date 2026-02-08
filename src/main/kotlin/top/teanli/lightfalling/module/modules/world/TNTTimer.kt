package top.teanli.lightfalling.module.modules.world

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import top.teanli.lightfalling.event.impl.TNTRenderEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.RenderTool
import java.awt.Color

class TNTTimer : Module("TNTTimer", "Shows a timer for TNT explosions", ModuleCategory.WORLD) {

    val tntRenderEvent = listen<TNTRenderEvent> { event ->

        if (event.tntEntityRenderState.fuseRemainingInTicks <= 0) {
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

        val color = getColorByTime(timeLeft)

        font.drawInBatch(
            text,
            xOffset,
            0.0f,
            color,
            true,
            event.poseStack.last().pose(),
            mc.renderBuffers().bufferSource(),
            Font.DisplayMode.NORMAL,
            0x60000000,
            LightTexture.FULL_BRIGHT,
        )

        event.poseStack.popPose()
    }

    private fun getColorByTime(timeLeft: Float): Int {
        return if (timeLeft > 2.0f) {
            -0xff0100
        } else if (timeLeft > 1.0f) {
            -0x100
        } else {
            -0x10000
        }
    }
}