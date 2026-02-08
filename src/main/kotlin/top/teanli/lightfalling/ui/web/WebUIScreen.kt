package top.teanli.lightfalling.ui.web

import net.ccbluex.liquidbounce.mcef.MCEF
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component

class WebUIScreen(val webUI: WebUI) : Screen(Component.literal("WebUI")) {

    override fun init() {
        super.init()
        webUI.browser?.setFocus(true)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val browser = webUI.browser ?: return

        // Update MCEF message loop
        MCEF.INSTANCE.app.handle.N_DoMessageLoopWork()
        
        if (browser.isTextureReady) {
            // Render the browser texture
            guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                browser.textureLocation,
                0, 0,
                0f, 0f,
                width, height,
                width, height
            )
        } else {
            // Draw a loading background if texture is not ready
            guiGraphics.fill(0, 0, width, height, -0x1000000)
            guiGraphics.drawCenteredString(font, "Loading Browser...", width / 2, height / 2, -1)
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        guiGraphics.drawString(font, "WebUI - ${webUI.url}", 5, 5, -1)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        webUI.browser?.let {
            it.sendKeyPress(keyEvent.key, keyEvent.scancode.toLong(), keyEvent.modifiers)
            it.setFocus(true)
            return true
        }
        return super.keyPressed(keyEvent)
    }

    override fun keyReleased(keyEvent: KeyEvent): Boolean {
        webUI.browser?.let {
            it.sendKeyRelease(keyEvent.key, keyEvent.scancode.toLong(), keyEvent.modifiers)
            it.setFocus(true)
            return true
        }
        return super.keyReleased(keyEvent)
    }

    override fun charTyped(charEvent: CharacterEvent): Boolean {
        webUI.browser?.let {
            it.sendKeyTyped(charEvent.codepoint.toChar(), charEvent.modifiers)
            it.setFocus(true)
            return true
        }
        return super.charTyped(charEvent)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        webUI.browser?.let {
            it.sendMousePress(mouseButtonEvent.x.toInt(), mouseButtonEvent.y.toInt(), mapButton(mouseButtonEvent.button()))
            it.setFocus(true)
            return true
        }
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        webUI.browser?.let {
            it.sendMouseRelease(mouseButtonEvent.x.toInt(), mouseButtonEvent.y.toInt(), mapButton(mouseButtonEvent.button()))
            it.setFocus(true)
            return true
        }
        return super.mouseReleased(mouseButtonEvent)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        webUI.browser?.sendMouseMove(mouseX.toInt(), mouseY.toInt())
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        webUI.browser?.sendMouseWheel(mouseX.toInt(), mouseY.toInt(), scrollY)
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    private fun mapButton(button: Int): Int {
        return when (button) {
            0 -> 0 // Left
            1 -> 2 // Right -> JCEF Right
            2 -> 1 // Middle -> JCEF Middle
            else -> button
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        webUI.resize(width, height)
    }

    override fun onClose() {
        webUI.close()
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false
}
