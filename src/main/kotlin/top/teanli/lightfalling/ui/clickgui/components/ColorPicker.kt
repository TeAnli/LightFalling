package top.teanli.lightfalling.ui.clickgui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import top.teanli.lightfalling.module.setting.ColorSetting
import java.awt.Color

class ColorPicker(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val setting: ColorSetting
) : AbstractWidget(x, y, width, height, Component.literal(setting.name)) {

    private var hue = 0f
    private var saturation = 0f
    private var brightness = 0f
    
    private var lastColor: Color? = null

    init {
        updateHSBFromSetting()
    }

    private fun updateHSBFromSetting() {
        val hsb = Color.RGBtoHSB(setting.value.red, setting.value.green, setting.value.blue, null)
        hue = hsb[0]
        saturation = hsb[1]
        brightness = hsb[2]
        lastColor = setting.value
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // 如果外部修改了颜色，更新内部 HSB 状态
        if (lastColor != setting.value) {
            updateHSBFromSetting()
        }

        val pickerWidth = width - 15
        val hueWidth = 10
        val spacing = 5

        drawSBPicker(guiGraphics, x, y, pickerWidth, height)

        drawHueSlider(guiGraphics, x + pickerWidth + spacing, y, hueWidth, height)
    }

    private fun drawSBPicker(guiGraphics: GuiGraphics, x: Int, y: Int, w: Int, h: Int) {
        val baseColor = Color.getHSBColor(hue, 1f, 1f)

        guiGraphics.fill(x, y, x + w, y + h, 0xFF000000.toInt())

        for (i in 0 until w) {
            val s = i.toFloat() / w
            val col = Color.getHSBColor(hue, s, 1f).rgb
            guiGraphics.fill(x + i, y, x + i + 1, y + h, col or 0xFF000000.toInt())
        }

        for (i in 0 until h) {
            val b = 1f - (i.toFloat() / h)
            val alpha = ((1f - b) * 255).toInt()
            val blackAlpha = (alpha shl 24) or 0x000000
            guiGraphics.fill(x, y + i, x + w, y + i + 1, blackAlpha)
        }

        val cursorX = x + (saturation * w).toInt()
        val cursorY = y + ((1f - brightness) * h).toInt()
        guiGraphics.renderOutline(cursorX - 2, cursorY - 2, 4, 4, 0xFFFFFFFF.toInt())
        guiGraphics.renderOutline(x - 1, y - 1, w + 2, h + 2, 0xFF555555.toInt())
    }

    private fun drawHueSlider(guiGraphics: GuiGraphics, x: Int, y: Int, w: Int, h: Int) {
        for (i in 0 until h) {
            val hVal = i.toFloat() / h
            guiGraphics.fill(x, y + i, x + w, y + i + 1, Color.getHSBColor(hVal, 1f, 1f).rgb or 0xFF000000.toInt())
        }

        val cursorY = y + (hue * h).toInt()
        guiGraphics.fill(x - 1, cursorY - 1, x + w + 1, cursorY + 1, 0xFFFFFFFF.toInt())
        guiGraphics.renderOutline(x - 1, y - 1, w + 2, h + 2, 0xFF555555.toInt())
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        updateValues(mouseButtonEvent.x, mouseButtonEvent.y)
    }

    override fun onDrag(
        mouseButtonEvent: MouseButtonEvent,
        d: Double,
        e: Double
    ) {
        updateValues(mouseButtonEvent.x, mouseButtonEvent.y)
    }

    private fun updateValues(mouseX: Double, mouseY: Double) {
        val pickerWidth = width - 15
        val hueWidth = 10
        val spacing = 5

        if (mouseX >= x + pickerWidth + spacing && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            hue = ((mouseY - y) / height).toFloat().coerceIn(0f, 1f)
        }
        else if (mouseX >= x && mouseX <= x + pickerWidth && mouseY >= y && mouseY <= y + height) {
            saturation = ((mouseX - x) / pickerWidth).toFloat().coerceIn(0f, 1f)
            brightness = (1f - ((mouseY - y) / height).toFloat()).coerceIn(0f, 1f)
        }

        val rgb = Color.getHSBColor(hue, saturation, brightness)
        setting.value = Color(rgb.red, rgb.green, rgb.blue, setting.value.alpha)
        lastColor = setting.value
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}
