package top.teanli.lightfalling.ui.clickgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.setting.*
import top.teanli.lightfalling.ui.clickgui.components.ColorPicker
import java.awt.Color
import kotlin.math.pow
import kotlin.math.round

class ModuleSettingsScreen(private val module: Module, private val parent: Screen) : Screen(Component.literal("Settings: ${module.name}")) {
    
    override fun init() {
        refreshLayout()
    }

    private fun refreshLayout() {
        clearWidgets()
        val centerX = width / 2
        var currentY = 50

        // Back button
        addRenderableWidget(
            Button.builder(Component.literal("Back")) {
                minecraft?.setScreen(parent)
            }.bounds(10, 10, 60, 20).build()
        )

        // Module Title
        addRenderableWidget(
            Button.builder(Component.literal("Settings: ${module.name}")) { }
                .bounds(centerX - 100, 20, 200, 20).build().apply {
                active = false
            }
        )

        module.settings.forEach { setting ->
            if (!setting.isVisible()) return@forEach

            when (setting) {
                is BooleanSetting -> {
                    addRenderableWidget(
                        Checkbox.builder(Component.literal(setting.name), font)
                            .pos(centerX - 75, currentY)
                            .selected(setting.value)
                            .onValueChange { _, value -> setting.value = value }
                            .build()
                    )
                    currentY += 25
                }
                is NumberSetting -> {
                    addRenderableWidget(
                        object : AbstractSliderButton(centerX - 75, currentY, 150, 20, Component.literal("${setting.name}: ${setting.value}"), ((setting.value - setting.min) / (setting.max - setting.min))) {
                            override fun updateMessage() {
                                message = Component.literal("${setting.name}: ${setting.value}")
                            }

                            override fun applyValue() {
                                val newVal = setting.min + value * (setting.max - setting.min)
                                val precisionFactor = 10.0.pow(setting.precision.toDouble())
                                setting.value = round(newVal * precisionFactor) / precisionFactor
                            }
                        }
                    )
                    currentY += 25
                }
                is ModeSetting -> {
                    addRenderableWidget(
                        Button.builder(Component.literal("${setting.name}: ${setting.value}")) { button ->
                            setting.nextMode()
                            button.message = Component.literal("${setting.name}: ${setting.value}")
                        }.bounds(centerX - 75, currentY, 150, 20).build()
                    )
                    currentY += 25
                }
                is ColorSetting -> {
                    // Color Label
                    addRenderableWidget(
                        Button.builder(Component.literal(setting.name)) { }
                            .bounds(centerX - 75, currentY, 150, 20).build().apply { active = false }
                    )
                    currentY += 22

                    // Color Picker Component
                    addRenderableWidget(
                        ColorPicker(centerX - 75, currentY, 150, 80, setting)
                    )
                    currentY += 85

                    // RGB Input Boxes
                    val boxWidth = 46
                    val boxSpacing = 6
                    
                    val rBox = object : EditBox(font, centerX - 75, currentY, boxWidth, 14, Component.literal("R")) {
                        override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
                            if (!isFocused && value != setting.value.red.toString()) {
                                value = setting.value.red.toString()
                            }
                            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
                        }
                    }
                    rBox.value = setting.value.red.toString()
                    rBox.setResponder { text ->
                        text.toIntOrNull()?.let { if (it in 0..255) setting.value = Color(it, setting.value.green, setting.value.blue, setting.value.alpha) }
                    }
                    addRenderableWidget(rBox)

                    val gBox = object : EditBox(font, centerX - 75 + boxWidth + boxSpacing, currentY, boxWidth, 14, Component.literal("G")) {
                        override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
                            if (!isFocused && value != setting.value.green.toString()) {
                                value = setting.value.green.toString()
                            }
                            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
                        }
                    }
                    gBox.value = setting.value.green.toString()
                    gBox.setResponder { text ->
                        text.toIntOrNull()?.let { if (it in 0..255) setting.value = Color(setting.value.red, it, setting.value.blue, setting.value.alpha) }
                    }
                    addRenderableWidget(gBox)

                    val bBox = object : EditBox(font, centerX - 75 + (boxWidth + boxSpacing) * 2, currentY, boxWidth, 14, Component.literal("B")) {
                        override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
                            if (!isFocused && value != setting.value.blue.toString()) {
                                value = setting.value.blue.toString()
                            }
                            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
                        }
                    }
                    bBox.value = setting.value.blue.toString()
                    bBox.setResponder { text ->
                        text.toIntOrNull()?.let { if (it in 0..255) setting.value = Color(setting.value.red, setting.value.green, it, setting.value.alpha) }
                    }
                    addRenderableWidget(bBox)

                    currentY += 25
                }
            }
        }
    }

    override fun mouseClicked(mouseEvent: MouseButtonEvent, bl: Boolean): Boolean {
        return super.mouseClicked(mouseEvent, bl)
    }

    override fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        return super.mouseReleased(mouseEvent)
    }

    override fun mouseDragged(mouseEvent: MouseButtonEvent, d: Double, e: Double): Boolean {
        return super.mouseDragged(mouseEvent, d, e)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        return super.keyPressed(keyEvent)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun isPauseScreen(): Boolean = false
}
