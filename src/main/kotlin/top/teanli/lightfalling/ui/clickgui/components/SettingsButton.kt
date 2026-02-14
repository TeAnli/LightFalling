package top.teanli.lightfalling.ui.clickgui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.ui.clickgui.ModuleSettingsScreen

class SettingsButton(
    val module: Module,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val parentScreen: Screen
) : Button(x, y, width, height, Component.empty(), { _ ->
    Minecraft.getInstance().setScreen(ModuleSettingsScreen(module, parentScreen))
}, DEFAULT_NARRATION) {


    init {
        setTooltip(Tooltip.create(Component.literal("Configure ${module.name}")))
    }

    override fun renderContents(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val iconSize = 16
        val iconX = x + (width - iconSize) / 2 + 1
        val iconY = y + (height - iconSize) / 2
        val gearIcon = Identifier.fromNamespaceAndPath("lightfalling", "textures/gui/gear.png")
        this.renderDefaultSprite(guiGraphics)
        val texture = Identifier.fromNamespaceAndPath("minecraft", "textures/block/deepslate.png")
//        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, iconX, iconY, 0, 0, 32, 32, iconSize, iconSize)
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, gearIcon, iconX, iconY, 0F, 0F, iconSize, iconSize, iconSize, iconSize);
    }
}
