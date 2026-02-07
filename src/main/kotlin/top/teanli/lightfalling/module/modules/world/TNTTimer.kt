package top.teanli.lightfalling.module.modules.world

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.LightmapTextureManager
import top.teanli.lightfalling.event.impl.TNTRenderEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.module.ModuleManager

class TNTTimer : Module("TNTTimer", "Shows a timer for TNT explosions", ModuleCategory.WORLD) {

    val tntRenderEvent = listen<TNTRenderEvent> { event ->

        if (event.tntEntityRenderState.fuse <= 0) {
            return@listen
        }
        val timeLeft: Float = event.tntEntityRenderState.fuse / 20.0f
        val text = String.format("%.1f", timeLeft)

        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        event.matrixStack.push()

        event.matrixStack.translate(0.5, 1.7, 0.5)

        val camera = client.gameRenderer.camera
        if (camera != null) {
            val rotation = camera.rotation
            event.matrixStack.multiply(rotation)
        }

        val scale = 0.025f
        event.matrixStack.scale(scale, -scale, scale)

        val textWidth = textRenderer.getWidth(text)
        val xOffset = -textWidth / 2.0f

        val color = getColorByTime(timeLeft)

        textRenderer.draw(
            text,
            xOffset,
            0.0f,
            color,
            true,
            event.matrixStack.peek().positionMatrix,
            client.bufferBuilders.entityVertexConsumers,
            TextRenderer.TextLayerType.NORMAL,
            0x60000000,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

        event.matrixStack.pop()
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