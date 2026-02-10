package top.teanli.lightfalling.ui.clickgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.module.ModuleManager
import kotlin.math.max
import kotlin.math.min

class ClickGUIScreen : Screen(Component.literal("LightFalling ClickGUI")) {
    private var selectedCategory: ModuleCategory = ModuleCategory.PLAYER
    private var bindingModule: Module? = null
    private var scrollOffset = 0.0
    private var isDraggingScrollbar = false

    private val tabButtons = mutableListOf<Button>()

    override fun init() {
        refreshLayout()
    }

    private fun refreshLayout() {
        clearWidgets()
        tabButtons.clear()
        val centerX = width / 2
        
        // Category Tabs at the top
        val totalTabsWidth = ModuleCategory.entries.size * 95 - 5
        var tabX = centerX - totalTabsWidth / 2
        
        ModuleCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            val tabBtn = Button.builder(Component.literal(category.categoryName.uppercase())) {
                selectedCategory = category
                scrollOffset = 0.0
                refreshLayout()
            }.bounds(tabX, 10, 90, 20).build().apply {
                active = !isSelected
            }
            tabButtons.add(tabBtn)
            addRenderableWidget(tabBtn)
            tabX += 95
        }

        // Modules vertically in the center
        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        val buttonWidth = 150
        val iconButtonWidth = 25
        val buttonHeight = 20
        val spacing = 5
        val totalRowWidth = buttonWidth + (spacing + iconButtonWidth) * 2
        val startX = centerX - totalRowWidth / 2
        var currentY = 50 + scrollOffset.toInt()

        modules.forEach { module ->
            val prefix = if (module.state) "§a" else "§c"
            val bindingText = if (bindingModule == module) "§b[...]" else if (module.key != 0) " §7[${GLFW.glfwGetKeyName(module.key, 0)?.uppercase() ?: module.key}]" else ""
            
            // Main Module Button (Middle)
            val moduleButton = Button.builder(Component.literal("$prefix${module.name}$bindingText")) { _ ->
                if (bindingModule == null) {
                    module.toggle()
                    refreshLayout()
                }
            }.bounds(startX + iconButtonWidth + spacing, currentY, buttonWidth, buttonHeight).build()
            
            moduleButton.setTooltip(Tooltip.create(Component.literal(module.description)))
            addRenderableWidget(moduleButton)

            // Toggle Button (Left)
            val toggleBtn = Button.builder(Component.literal(if (module.state) "ON" else "OFF")) { _ ->
                module.toggle()
                refreshLayout()
            }.bounds(startX, currentY, iconButtonWidth, buttonHeight).build()
            addRenderableWidget(toggleBtn)

            // Settings Button (Right)
            val settingsBtn = Button.builder(Component.literal("S")) { _ ->
                minecraft?.setScreen(ModuleSettingsScreen(module, this))
            }.bounds(startX + iconButtonWidth + spacing + buttonWidth + spacing, currentY, iconButtonWidth, buttonHeight).build().apply {
                setTooltip(Tooltip.create(Component.literal("Open Settings")))
            }
            addRenderableWidget(settingsBtn)
            
            currentY += buttonHeight + spacing
        }
    }

    private fun getContentHeight(): Int {
        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        return modules.size * (20 + 5)
    }

    private fun getViewHeight(): Int = height - 80

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val contentHeight = getContentHeight()
        val viewHeight = getViewHeight()
        
        if (contentHeight > viewHeight) {
            scrollOffset += scrollY * 20
            scrollOffset = min(0.0, max(scrollOffset, (viewHeight - contentHeight).toDouble()))
            refreshLayout()
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun mouseClicked(mouseEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (bindingModule != null) return false

        val centerX = width / 2
        val buttonWidth = 150
        val iconButtonWidth = 25
        val spacing = 5
        val totalRowWidth = buttonWidth + (spacing + iconButtonWidth) * 2
        val bgX = centerX - totalRowWidth / 2 - 10
        val bgW = totalRowWidth + 20
        
        // Scrollbar interaction
        val scrollbarX = bgX + bgW - 6
        val viewHeight = getViewHeight()
        val contentHeight = getContentHeight()
        
        if (contentHeight > viewHeight && mouseEvent.x >= scrollbarX && mouseEvent.x <= scrollbarX + 4 && mouseEvent.y >= 40 && mouseEvent.y <= 40 + viewHeight) {
            isDraggingScrollbar = true
            updateScrollFromMouse(mouseEvent.y)
            return true
        }

        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        val startX = centerX - totalRowWidth / 2
        val startY = 50 + scrollOffset.toInt()

        modules.forEachIndexed { index, module ->
            val y = startY + index * (20 + spacing)
            val x = startX + iconButtonWidth + spacing

            if (y >= 40 && y <= height - 40) {
                if (mouseEvent.x >= x && mouseEvent.x <= x + buttonWidth && mouseEvent.y >= y && mouseEvent.y <= y + 20) {
                    if (mouseEvent.button() == 2) { // Middle Click
                        bindingModule = module
                        refreshLayout()
                        return true
                    }
                }
            }
        }
        
        return super.mouseClicked(mouseEvent, bl)
    }

    override fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        isDraggingScrollbar = false
        return super.mouseReleased(mouseEvent)
    }

    override fun mouseDragged(mouseEvent: MouseButtonEvent, d: Double, e: Double): Boolean {
        if (isDraggingScrollbar) {
            updateScrollFromMouse(mouseEvent.y)
            return true
        }
        return super.mouseDragged(mouseEvent, d, e)
    }

    private fun updateScrollFromMouse(mouseY: Double) {
        val viewHeight = getViewHeight()
        val contentHeight = getContentHeight()
        val trackY = 40
        
        val percentage = (mouseY - trackY) / viewHeight
        val targetOffset = -(percentage * contentHeight - viewHeight / 2)
        scrollOffset = min(0.0, max(targetOffset, (viewHeight - contentHeight).toDouble()))
        refreshLayout()
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        if (bindingModule != null) {
            if (keyEvent.key == GLFW.GLFW_KEY_ESCAPE || keyEvent.key == GLFW.GLFW_KEY_DELETE || keyEvent.key == GLFW.GLFW_KEY_BACKSPACE) {
                bindingModule?.key = 0
            } else {
                bindingModule?.key = keyEvent.key
            }
            bindingModule = null
            refreshLayout()
            return true
        }
        return super.keyPressed(keyEvent)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderTransparentBackground(guiGraphics)
        
        val centerX = width / 2
        val buttonWidth = 150
        val iconButtonWidth = 25
        val spacing = 5
        val totalRowWidth = buttonWidth + (spacing + iconButtonWidth) * 2
        
        // 2. Draw Category Tabs (Outside scissor)
        tabButtons.forEach { it.render(guiGraphics, mouseX, mouseY, partialTick) }

        // 3. Draw list background
        val bgX = centerX - totalRowWidth / 2 - 10
        val bgY = 40
        val bgW = totalRowWidth + 20
        val bgH = height - 80
        
        guiGraphics.fill(bgX, bgY, bgX + bgW, bgY + bgH, 0x90000000.toInt())
        
        // 4. Draw Scrollbar
        val contentHeight = getContentHeight()
        val viewHeight = getViewHeight()
        if (contentHeight > viewHeight) {
            val scrollbarX = bgX + bgW - 6
            val scrollbarHeight = max(20.0, (viewHeight.toDouble() / contentHeight) * viewHeight).toInt()
            val scrollbarY = bgY + ((-scrollOffset / (contentHeight - viewHeight)) * (viewHeight - scrollbarHeight)).toInt()
            
            // Track
            guiGraphics.fill(scrollbarX, bgY, scrollbarX + 4, bgY + bgH, 0x40FFFFFF.toInt())
            // Thumb
            guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarHeight, 0xA0FFFFFF.toInt())
        }
        
        // 5. Scissor and render module list
        guiGraphics.enableScissor(bgX, bgY, bgX + bgW, bgY + bgH)
        children().forEach { 
            if (it is Button && !tabButtons.contains(it)) {
                it.render(guiGraphics, mouseX, mouseY, partialTick)
            }
        }
        guiGraphics.disableScissor()
        
        // 6. Draw instructions
        guiGraphics.drawCenteredString(font, "§7Left: Toggle | Right: Bind (on Main) | Icons: Quick Actions", centerX, height - 20, -1)
        
        if (bindingModule != null) {
            guiGraphics.fill(0, 0, width, height, 0x70000000)
            guiGraphics.drawCenteredString(font, "Press any key to bind §b${bindingModule?.name}§r...", centerX, height / 2, -1)
            guiGraphics.drawCenteredString(font, "§7Press ESC to unbind", centerX, height / 2 + 15, -1)
        }
    }

    override fun isPauseScreen(): Boolean = false
}
