package top.teanli.lightfalling.tool

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import java.awt.Color

object RenderTool {
    private val mc: MinecraftClient = MinecraftClient.getInstance()

    /**
     * Draws text with a shadow.
     */
    fun drawText(context: DrawContext, text: String, x: Int, y: Int, color: Int = Color.WHITE.rgb, shadow: Boolean = true) {
        if (shadow) {
            context.drawTextWithShadow(mc.textRenderer, text, x, y, color)
        } else {
            context.drawText(mc.textRenderer, text, x, y, color, false)
        }
    }

    /**
     * Draws text with a shadow using a Color object.
     */
    fun drawText(context: DrawContext, text: String, x: Int, y: Int, color: Color, shadow: Boolean = true) {
        drawText(context, text, x, y, color.rgb, shadow)
    }

    /**
     * Gets the width of the given text.
     */
    fun getTextWidth(text: String): Int {
        return mc.textRenderer.getWidth(text)
    }

    /**
     * Gets the height of the font.
     */
    fun getFontHeight(): Int {
        return mc.textRenderer.fontHeight
    }
}
