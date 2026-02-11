package top.teanli.lightfalling.ui.clickgui.components

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import top.teanli.lightfalling.module.setting.BlockListSetting
import kotlin.math.max
import kotlin.math.min

class BlockSelector(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val setting: BlockListSetting
) : AbstractWidget(x, y, width, height, Component.literal("Block Selector")) {
    
    private val mc = Minecraft.getInstance()
    private var scrollOffset = 0
    private val itemSize = 18
    private val itemsPerRow = (width - 10) / itemSize
    private val visibleRows = (height - 30) / itemSize
    
    // List of common blocks
    private val commonBlocks = listOf(
        Blocks.STONE, Blocks.DEEPSLATE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE,
        Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE,
        Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
        Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.COPPER_ORE,
        Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
        Blocks.ANCIENT_DEBRIS, Blocks.NETHERRACK, Blocks.END_STONE,
        Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.GRAVEL,
        Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG,
        Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.CHERRY_LOG, Blocks.MANGROVE_LOG,
        Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.OBSIDIAN
    )
    
    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Draw background
        guiGraphics.fill(x, y, x + width, y + height, 0x80000000.toInt())
        guiGraphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF.toInt())
        guiGraphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF.toInt())
        guiGraphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF.toInt())
        guiGraphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF.toInt())
        
        // Draw title
        guiGraphics.drawString(mc.font, "Select Blocks to Mine:", x + 5, y + 5, 0xFFFFFF)
        
        // Draw block grid
        val startY = y + 20
        val totalRows = (commonBlocks.size + itemsPerRow - 1) / itemsPerRow
        val maxScroll = max(0, totalRows - visibleRows)
        scrollOffset = scrollOffset.coerceIn(0, maxScroll)
        
        for (i in 0 until min(commonBlocks.size, itemsPerRow * visibleRows)) {
            val index = i + scrollOffset * itemsPerRow
            if (index >= commonBlocks.size) break
            
            val block = commonBlocks[index]
            val row = i / itemsPerRow
            val col = i % itemsPerRow
            
            val itemX = x + 5 + col * itemSize
            val itemY = startY + row * itemSize
            
            // Check if selected
            val isSelected = setting.containsBlock(block)
            
            // Draw background
            val bgColor = if (isSelected) 0x8000FF00.toInt() else 0x80404040.toInt()
            guiGraphics.fill(itemX, itemY, itemX + 16, itemY + 16, bgColor)
            
            // Highlight on mouse hover
            if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                guiGraphics.fill(itemX, itemY, itemX + 16, itemY + 16, 0x80FFFFFF.toInt())
            }
            
            // Draw block icon
            val itemStack = ItemStack(block.asItem())
            guiGraphics.renderItem(itemStack, itemX, itemY)
        }
    }

    
    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (!isMouseOver(mouseButtonEvent.x, mouseButtonEvent.y)) return false
        
        val startY = y + 20
        val relativeX = (mouseButtonEvent.x - x - 5).toInt()
        val relativeY = (mouseButtonEvent.y - startY).toInt()
        
        if (relativeX < 0 || relativeY < 0) return false
        
        val col = relativeX / itemSize
        val row = relativeY / itemSize
        
        if (col >= itemsPerRow || row >= visibleRows) return false
        
        val index = (row + scrollOffset) * itemsPerRow + col
        if (index >= commonBlocks.size) return false
        
        val block = commonBlocks[index]
        setting.toggleBlock(block)
        
        return true
    }
    
    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (!isMouseOver(mouseX, mouseY)) return false
        
        val totalRows = (commonBlocks.size + itemsPerRow - 1) / itemsPerRow
        val maxScroll = max(0, totalRows - visibleRows)
        
        scrollOffset = (scrollOffset - scrollY.toInt()).coerceIn(0, maxScroll)
        
        return true
    }
    
    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        narrationElementOutput.add(net.minecraft.client.gui.narration.NarratedElementType.TITLE, message)
    }
}
