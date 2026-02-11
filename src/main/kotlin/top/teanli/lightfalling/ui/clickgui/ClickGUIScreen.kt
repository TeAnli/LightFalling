package top.teanli.lightfalling.ui.clickgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.WidgetTooltipHolder
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.ui.clickgui.components.SettingsButton
import top.teanli.lightfalling.tool.I18n
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

class ClickGUIScreen : Screen(I18n.component("lightfalling.gui.clickgui")) {
    private var selectedCategory: ModuleCategory = ModuleCategory.PLAYER
    private var bindingModule: Module? = null
    private var scrollOffset = 0.0
    private var isDraggingScrollbar = false

    private val tabButtons = mutableListOf<CategoryTab>()

    inner class CategoryTab(
        val category: ModuleCategory,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) : AbstractButton(x, y, width, height, I18n.component("lightfalling.category.${category.categoryName.lowercase()}")) {
        
        override fun onPress(inputWithModifiers: InputWithModifiers) {
            selectedCategory = category
            scrollOffset = 0.0
            this@ClickGUIScreen.refreshLayout()
        }

        override fun renderContents(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
            val isSelected = selectedCategory == category
            val color = if (isSelected) 0xAA000000.toInt() else if (isHovered) 0x70000000 else 0x40000000
            val borderColor = if (isSelected) 0xFF555555.toInt() else if (isHovered) -1 else 0xFF333333.toInt()

            val renderY = if (isSelected) y - 2 else y
            val renderHeight = if (isSelected) height + 2 else height

            guiGraphics.fill(x, renderY, x + width, renderY + renderHeight, color)

            guiGraphics.fill(x, renderY, x + 1, renderY + renderHeight, borderColor) // 左
            guiGraphics.fill(x, renderY, x + width, renderY + 1, borderColor) // 上
            guiGraphics.fill(x + width - 1, renderY, x + width, renderY + renderHeight, borderColor) // 右

            if (!isSelected) {
                guiGraphics.fill(x, renderY + renderHeight - 1, x + width, renderY + renderHeight, borderColor)
            }else{
                guiGraphics.fill(x + (width / 2) - 10, renderY + renderHeight - 1, x + (width / 2) + 10, renderY + renderHeight, -1)
            }

            // 绘制文字 - 使用 drawCenteredString 确保渲染
            val textX = x + width / 2
            val textY = renderY + (renderHeight - 8) / 2
            guiGraphics.drawCenteredString(font, message, textX, textY, if (isSelected) -1 else Color(200, 200, 200).rgb)
        }




        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
    }

    override fun init() {
        refreshLayout()
    }

    private fun refreshLayout() {
        clearWidgets()
        tabButtons.clear()
        val centerX = width / 2
        
        val categories = ModuleCategory.entries
        val tabWidth = 70
        val tabHeight = 20
        val tabSpacing = 4
        val totalTabsWidth = categories.size * tabWidth + (categories.size - 1) * tabSpacing
        var tabX = centerX - totalTabsWidth / 2

        // 1. 先添加选项卡，确保它们在 children 列表的前面，优先接收点击事件
        categories.forEach { category ->
            val tab = CategoryTab(category, tabX, 25, tabWidth, tabHeight)
            tabButtons.add(tab)
            addRenderableWidget(tab)
            tabX += tabWidth + tabSpacing
        }

        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        val listWidth = 300
        val buttonHeight = 24
        val rowSpacing = 6
        val startX = centerX - listWidth / 2
        
        val toggleWidth = 40
        val settingsWidth = 25
        val mainBtnWidth = listWidth - toggleWidth - settingsWidth - (rowSpacing * 2)
        
        var currentY = 50 + scrollOffset.toInt()

        // 2. 添加模块相关按钮
        modules.forEach { module ->
            val stateColor = if (module.state) "§a" else "§7"
            val bindingText = if (bindingModule == module) "§b[...]" else if (module.key != 0) " §8[${GLFW.glfwGetKeyName(module.key, 0)?.uppercase() ?: module.key}]" else ""
            
            val toggleBtn = Button.builder(Component.literal(if (module.state) "ON" else "OFF")) { _ ->
                module.toggle()
                refreshLayout()
            }.bounds(startX, currentY, toggleWidth, buttonHeight).build()
            addRenderableWidget(toggleBtn)

            val moduleButton = Button.builder(Component.literal("$stateColor${module.getDisplayName()}$bindingText")) { _ ->
                if (bindingModule == null) {
                    module.toggle()
                    refreshLayout()
                }
            }.bounds(startX + toggleWidth + rowSpacing, currentY, mainBtnWidth, buttonHeight).build()
            
            moduleButton.setTooltip(Tooltip.create(Component.literal(module.getDisplayDescription())))
            addRenderableWidget(moduleButton)

            val settingsBtn = SettingsButton(module, startX + toggleWidth + mainBtnWidth + rowSpacing * 2, currentY, settingsWidth, buttonHeight, this)
            addRenderableWidget(settingsBtn)
            
            currentY += buttonHeight + rowSpacing
        }
    }

    private fun getContentHeight(): Int {
        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        return modules.size * (24 + 6) + 10
    }

    private fun getViewHeight(): Int = height - 90 // 与渲染区域 bgH 保持一致

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        // 只有在列表区域内才响应滚动
        val centerX = width / 2
        val listWidth = 320
        if (mouseX < centerX - listWidth / 2 || mouseX > centerX + listWidth / 2 || mouseY < 45 || mouseY > height - 45) {
            return false
        }

        val contentHeight = getContentHeight()
        val viewHeight = getViewHeight()
        
        if (contentHeight > viewHeight) {
            scrollOffset += scrollY * 24
            scrollOffset = min(0.0, max(scrollOffset, (viewHeight - contentHeight).toDouble()))
            refreshLayout()
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun mouseClicked(mouseEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (bindingModule != null) return false

        val centerX = width / 2
        val listWidth = 320
        val bgX = centerX - listWidth / 2
        val bgW = listWidth
        val bgY = 45
        val bgH = height - 90
        
        // 1. 优先检查选项卡点击
        for (tab in tabButtons) {
            if (tab.isMouseOver(mouseEvent.x, mouseEvent.y)) {
                return tab.mouseClicked(mouseEvent, bl)
            }
        }

        // 2. 检查滚动条点击
        val scrollbarX = bgX + bgW - 8
        val contentHeight = getContentHeight()
        val viewHeight = getViewHeight()
        
        if (contentHeight > viewHeight && mouseEvent.x >= scrollbarX && mouseEvent.x <= scrollbarX + 6 && mouseEvent.y >= bgY && mouseEvent.y <= bgY + viewHeight) {
            isDraggingScrollbar = true
            updateScrollFromMouse(mouseEvent.y)
            return true
        }

        // 3. 检查模块列表区域内的点击
        // 如果点击位置不在裁剪区域内，直接拦截，不向下传递给模块按钮
        if (mouseEvent.x < bgX || mouseEvent.x > bgX + bgW || mouseEvent.y < bgY || mouseEvent.y > bgY + bgH) {
            return false
        }

        // 处理中键绑定逻辑
        val modules = ModuleManager.getModulesByCategory(selectedCategory)
        val startX = centerX - 300 / 2
        val toggleWidth = 40
        val rowSpacing = 6
        val startY = 50 + scrollOffset.toInt()

        modules.forEachIndexed { index, module ->
            val y = startY + index * (24 + rowSpacing)
            val x = startX + toggleWidth + rowSpacing
            val mainBtnWidth = 300 - 40 - 25 - (6 * 2)

            if (mouseEvent.x >= x && mouseEvent.x <= x + mainBtnWidth && mouseEvent.y >= y && mouseEvent.y <= y + 24) {
                if (mouseEvent.button() == 2) {
                    bindingModule = module
                    refreshLayout()
                    return true
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
        val trackY = 45
        
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
        if (keyEvent.key == GLFW.GLFW_KEY_ESCAPE) {
            onClose()
            return true
        }
        return super.keyPressed(keyEvent)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderTransparentBackground(guiGraphics)
        
        val centerX = width / 2
        val listWidth = 320
        val bgX = centerX - listWidth / 2
        val bgY = 45
        val bgW = listWidth
        val bgH = height - 90
        
        // 1. 渲染模块列表容器背景
        guiGraphics.fill(bgX, bgY, bgX + bgW, bgY + bgH, 0xAA000000.toInt())
        
        // 2. 绘制边框 (支持选中选项卡处的自动断开融合)
        val borderColor = 0xFF555555.toInt()
        val selectedTab = tabButtons.find { it.category == selectedCategory }
        
//        if (selectedTab != null) {
        // 顶部边框 - 分段绘制以避开选中的选项卡
        if (selectedTab!!.x > bgX) {
            guiGraphics.fill(bgX - 1, bgY - 1, selectedTab.x, bgY, borderColor)
        }
        if (selectedTab.x + selectedTab.width < bgX + bgW) {
            guiGraphics.fill(selectedTab.x + selectedTab.width, bgY - 1, bgX + bgW + 1, bgY, borderColor)
        }
//        } else {
//            guiGraphics.fill(bgX - 1, bgY - 1, bgX + bgW + 1, bgY, borderColor)
//        }
//
        // 绘制左、右、底部的边框
        guiGraphics.fill(bgX - 1, bgY, bgX, bgY + bgH, borderColor) // 左
        guiGraphics.fill(bgX + bgW, bgY, bgX + bgW + 1, bgY + bgH, borderColor) // 右
        guiGraphics.fill(bgX - 1, bgY + bgH, bgX + bgW + 1, bgY + bgH + 1, borderColor) // 下

        // 3. 渲染选项卡 (覆盖在列表顶部边框上)
        tabButtons.forEach { it.render(guiGraphics, mouseX, mouseY, partialTick) }
        
        // 3. 绘制滚动条
        val contentHeight = getContentHeight()
        val viewHeight = getViewHeight()
        if (contentHeight > viewHeight) {
            val scrollbarX = bgX + bgW - 8
            val scrollbarHeight = max(20.0, (viewHeight.toDouble() / contentHeight) * viewHeight).toInt()
            val scrollbarY = bgY + ((-scrollOffset / (contentHeight - viewHeight)) * (viewHeight - scrollbarHeight)).toInt()
            
            guiGraphics.fill(scrollbarX, bgY + 2, scrollbarX + 6, bgY + bgH - 2, 0x30FFFFFF.toInt())
            guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + 6, scrollbarY + scrollbarHeight, 0x80FFFFFF.toInt())
        }
        
        // 4. 裁剪并渲染模块列表
        guiGraphics.enableScissor(bgX, bgY, bgX + bgW, bgY + bgH)
        children().forEach {
            // 只渲染模块相关的按钮（即不在 tabButtons 中的按钮）
            if (it is Button) {
                it.render(guiGraphics, mouseX, mouseY, partialTick)
            }
        }
        guiGraphics.disableScissor()
        
        val hintY = height - 30
        guiGraphics.drawCenteredString(font, I18n.translate("lightfalling.gui.clickgui.hint.bind"), centerX, hintY, -1)
        guiGraphics.drawCenteredString(font, I18n.translate("lightfalling.gui.clickgui.footer"), centerX, hintY + 12, -1)
        
        if (bindingModule != null) {
            guiGraphics.fill(0, 0, width, height, 0xCC000000.toInt())
            guiGraphics.drawCenteredString(font, I18n.translate("lightfalling.gui.clickgui.binding.title", bindingModule?.getDisplayName() ?: ""), centerX, height / 2 - 10, -1)
            guiGraphics.drawCenteredString(font, I18n.translate("lightfalling.gui.clickgui.binding.hint"), centerX, height / 2 + 10, -1)
        }
    }

    override fun isPauseScreen(): Boolean = false
}
