package top.teanli.lightfalling.module.modules.display

import net.minecraft.core.Direction
import net.minecraft.world.level.block.*
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import top.teanli.lightfalling.event.impl.Render2DEvent
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.ColorTool
import top.teanli.lightfalling.tool.RedstoneRenderer
import top.teanli.lightfalling.tool.RenderTool
import java.awt.Color

class RedstoneIndicator : Module(
    "RedstoneIndicator",
    "Displays redstone signal strength of the block you're looking at",
    ModuleCategory.DISPLAY
) {
    private val show2D = checkbox("show2d", true)
    private val show3D = checkbox("show3d", true)
    private val showAllSides = checkbox("showallsides", false)
    private val scanRange = slider("scanrange", 16.0, 4.0, 32.0, 0)
    private val shadow = checkbox("shadow", true)

    // List of redstone-related blocks
    private fun isRedstoneBlock(block: Block): Boolean {
        return RedstoneRenderer.isRedstoneBlock(block)
    }

    private val onRender3D = listen<Render3DEvent> { event ->
        if (!show3D.value) return@listen
        
        val player = mc.player ?: return@listen
        val level = mc.level ?: return@listen
        val poseStack = event.poseStack
        val buffer = event.buffer
        val camera = event.camera

        val playerPos = player.blockPosition()
        val range = scanRange.value.toInt()
        
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val pos = playerPos.offset(x, y, z)
                    val blockState = level.getBlockState(pos)
                    
                    if (blockState.isAir) continue
                    
                    val redstoneInfo = RedstoneRenderer.getRedstoneInfo(blockState, pos) ?: continue
                    
                    RedstoneRenderer.renderRedstoneText(
                        poseStack,
                        buffer,
                        pos,
                        camera.position(),
                        redstoneInfo
                    )
                }
            }
        }
    }

    private val onRender2D = listen<Render2DEvent> { event ->
        if (!show2D.value) return@listen

        val player = mc.player ?: return@listen
        val level = mc.level ?: return@listen
        val guiGraphics = event.guiGraphics

        val hitResult = mc.hitResult
        if (hitResult == null || hitResult.type != HitResult.Type.BLOCK) return@listen

        val blockHitResult = hitResult as BlockHitResult
        val pos = blockHitResult.blockPos
        val blockState = level.getBlockState(pos)

        if (blockState.isAir) return@listen

        // Only show redstone-related blocks
        if (!isRedstoneBlock(blockState.block)) return@listen

        // Get redstone signal strength of the block
        val directSignal = level.getDirectSignalTo(pos)
        val signal = level.getBestNeighborSignal(pos)

        // Check if it's redstone wire
        val isRedstoneWire = blockState.block is RedStoneWireBlock
        val wireSignal = if (isRedstoneWire) {
            blockState.getValue(RedStoneWireBlock.POWER)
        } else null

        // Calculate top-right position
        val screenWidth = mc.window.guiScaledWidth
        val padding = 10
        var currentY = padding

        // Collect all text to display
        val lines = mutableListOf<Pair<String, Color>>()

        // Display block name
        val blockName = blockState.block.name.string
        lines.add("Block: $blockName" to Color.WHITE)

        // Display redstone wire signal strength
        if (wireSignal != null) {
            val color = ColorTool.getSignalColor(wireSignal)
            lines.add("Wire Power: $wireSignal" to color)
        }

        // Display strongest signal received by block
        if (signal > 0) {
            val color = ColorTool.getSignalColor(signal)
            lines.add("Received Signal: $signal" to color)
        }

        // Display direct signal emitted by block
        if (directSignal > 0) {
            val color = ColorTool.getSignalColor(directSignal)
            lines.add("Direct Signal: $directSignal" to color)
        }

        // Display signal strength from each direction
        if (showAllSides.value) {
            lines.add("" to Color.WHITE) // Empty line
            lines.add("Signal by Direction:" to Color.LIGHT_GRAY)

            for (direction in Direction.entries) {
                val neighborPos = pos.relative(direction)
                val neighborSignal = level.getSignal(neighborPos, direction)

                if (neighborSignal > 0) {
                    val color = ColorTool.getSignalColor(neighborSignal)
                    lines.add("  ${direction.name}: $neighborSignal" to color)
                }
            }
        }

        // Render all text (right-aligned)
        for ((text, color) in lines) {
            if (text.isEmpty()) {
                currentY += RenderTool.getFontHeight() / 2
                continue
            }

            val textWidth = RenderTool.getTextWidth(text)
            val x = screenWidth - textWidth - padding

            RenderTool.drawText(
                guiGraphics,
                text,
                x,
                currentY,
                color.rgb,
                shadow.value
            )
            currentY += RenderTool.getFontHeight() + 2
        }
    }
}
