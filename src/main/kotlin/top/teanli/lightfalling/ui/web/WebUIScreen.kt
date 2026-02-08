package top.teanli.lightfalling.ui.web

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import net.minecraft.text.Text

class WebUIScreen(val webUI: WebUI) : Screen(Text.literal("WebUI")) {

    override fun init() {
        super.init()
        webUI.resize(width, height)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val browser = webUI.browser ?: return

        if (browser.isTextureReady) {
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                browser.textureLocation,
                0, 0,
                0f, 0f,
                width, height,
                width, height
            )
        }

        browser.sendMouseMove(mouseX, mouseY)
    }


    override fun keyPressed(input: KeyInput): Boolean {
        webUI.browser?.sendKeyPress(input.key, input.scancode.toLong(), input.modifiers)
        return super.keyPressed(input)
    }

    override fun keyReleased(input: KeyInput): Boolean {
        webUI.browser?.sendKeyRelease(input.key, input.scancode.toLong(), input.modifiers)
        return super.keyReleased(input)
    }

    override fun charTyped(input: CharInput): Boolean {
        webUI.browser?.sendKeyTyped(input.codepoint.toChar(), input.modifiers)
        return super.charTyped(input)
    }


    override fun mouseReleased(click: Click): Boolean {
        webUI.browser?.sendMouseRelease(click.x.toInt() , click.y.toInt(), click.button())
        return super.mouseReleased(click)
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        webUI.browser?.sendMousePress(click.x.toInt(), click.y.toInt(), click.button())
        return super.mouseClicked(click, doubled)
    }

    override fun close() {
        webUI.close()
        super.close()
    }

    override fun shouldPause(): Boolean = false
}
