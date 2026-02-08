package top.teanli.lightfalling.tool

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import java.awt.Color

object RenderTool {
    private val mc: Minecraft = Minecraft.getInstance()

    /**
     * Draws text with a shadow.
     */
    fun drawText(guiGraphics: GuiGraphics, text: String, x: Int, y: Int, color: Int = Color.WHITE.rgb, shadow: Boolean = true) {
        if (shadow) {
            guiGraphics.drawString(mc.font, text, x, y, color)
        } else {
            guiGraphics.drawString(mc.font, text, x, y, color, false)
        }
    }

    /**
     * Draws text with a shadow using a Color object.
     */
    fun drawText(guiGraphics: GuiGraphics, text: String, x: Int, y: Int, color: Color, shadow: Boolean = true) {
        drawText(guiGraphics, text, x, y, color.rgb, shadow)
    }

    /**
     * Gets the width of the given text.
     */
    fun getTextWidth(text: String): Int {
        return mc.font.width(text)
    }

    /**
     * Gets the height of the font.
     */
    fun getFontHeight(): Int {
        return mc.font.lineHeight
    }
}
