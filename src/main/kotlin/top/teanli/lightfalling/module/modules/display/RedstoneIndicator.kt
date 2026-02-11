package top.teanli.lightfalling.module.modules.display

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.piston.PistonBaseBlock
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import top.teanli.lightfalling.event.impl.Render2DEvent
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
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
        return block is RedStoneWireBlock ||
               block is RedstoneTorchBlock ||
               block is RedstoneWallTorchBlock ||
               block is RepeaterBlock ||
               block is ComparatorBlock ||
               block is RedstoneLampBlock ||
               block is ObserverBlock ||
               block is PistonBaseBlock ||
               block is DispenserBlock ||
               block is DropperBlock ||
               block is HopperBlock ||
               block is DoorBlock ||
               block is TrapDoorBlock ||
               block is FenceGateBlock ||
               block is NoteBlock ||
               block is RedStoneOreBlock ||
               block is LeverBlock ||
               block is ButtonBlock ||
               block is PressurePlateBlock ||
               block is WeightedPressurePlateBlock ||
               block is TripWireBlock ||
               block is TripWireHookBlock ||
               block is DaylightDetectorBlock ||
               block is TargetBlock ||
               block is LightningRodBlock ||
               block is LecternBlock ||
               block is BellBlock
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
            val color = getSignalColor(wireSignal)
            lines.add("Wire Power: $wireSignal" to color)
        }

        // Display strongest signal received by block
        if (signal > 0) {
            val color = getSignalColor(signal)
            lines.add("Received Signal: $signal" to color)
        }

        // Display direct signal emitted by block
        if (directSignal > 0) {
            val color = getSignalColor(directSignal)
            lines.add("Direct Signal: $directSignal" to color)
        }

        // Display signal strength from each direction
        if (showAllSides.value) {
            lines.add("" to Color.WHITE) // Empty line
            lines.add("Signal by Direction:" to Color.LIGHT_GRAY)

            for (direction in Direction.values()) {
                val neighborPos = pos.relative(direction)
                val neighborSignal = level.getSignal(neighborPos, direction)
                
                if (neighborSignal > 0) {
                    val color = getSignalColor(neighborSignal)
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

    private val onRender3D = listen<Render3DEvent> { event ->
        if (!show3D.value) return@listen
        
        val player = mc.player ?: return@listen
        val level = mc.level ?: return@listen
        val poseStack = event.poseStack
        val camera = event.camera
        val cameraPos = camera.position()

        val playerPos = player.blockPosition()
        val range = scanRange.value.toInt()

        // Scan redstone blocks within range
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val pos = playerPos.offset(x, y, z)
                    val blockState = level.getBlockState(pos)
                    
                    if (blockState.isAir || !isRedstoneBlock(blockState.block)) continue

                    // Get signal strength
                    val signal = level.getBestNeighborSignal(pos)
                    val directSignal = level.getDirectSignalTo(pos)
                    
                    // Special handling for redstone wire
                    val wireSignal = if (blockState.block is RedStoneWireBlock) {
                        blockState.getValue(RedStoneWireBlock.POWER)
                    } else null
                    
                    val displaySignal = wireSignal ?: signal.coerceAtLeast(directSignal)
                    
                    // Draw block border
                    val color = getSignalColor(displaySignal)
                    drawBlockBox(poseStack, event.buffer, pos, cameraPos, color, displaySignal)
                }
            }
        }
    }

    private fun drawBlockBox(
        poseStack: PoseStack,
        buffer: net.minecraft.client.renderer.MultiBufferSource,
        pos: BlockPos,
        cameraPos: Vec3,
        color: Color,
        signal: Int
    ) {
        poseStack.pushPose()

        val x = pos.x - cameraPos.x
        val y = pos.y - cameraPos.y
        val z = pos.z - cameraPos.z

        poseStack.translate(x, y, z)

        val aabb = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        
        // Adjust transparency based on signal strength
        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f

        // Draw border


        poseStack.popPose()

        // Draw signal strength text
        if (signal > 0) {
            drawSignalText(poseStack, buffer, pos, cameraPos, signal, color)
        }
    }

    private fun drawSignalText(
        poseStack: PoseStack,
        buffer: net.minecraft.client.renderer.MultiBufferSource,
        pos: BlockPos,
        cameraPos: Vec3,
        signal: Int,
        color: Color
    ) {
        val font: Font = mc.font
        val camera = mc.gameRenderer.mainCamera

        poseStack.pushPose()

        val x = pos.x + 0.5 - cameraPos.x
        val y = pos.y + 0.5 - cameraPos.y
        val z = pos.z + 0.5 - cameraPos.z

        poseStack.translate(x.toFloat(), y.toFloat(), z.toFloat())
        poseStack.mulPose(camera.rotation())

        val scale = 0.025f
        poseStack.scale(scale, -scale, scale)

        val text = signal.toString()
        val width = font.width(text)
        
        val colorInt = color.rgb or (255 shl 24)

        font.drawInBatch(
            text,
            -width / 2f,
            0f,
            colorInt,
            true,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.SEE_THROUGH,
            0,
            15728880
        )

        poseStack.popPose()
    }

    private fun getSignalColor(signal: Int): Color {
        return when {
            signal == 0 -> Color.GRAY
            signal <= 3 -> Color(255, 100, 100) // Light red
            signal <= 7 -> Color(255, 200, 0)   // Orange
            signal <= 11 -> Color(255, 255, 0)  // Yellow
            signal <= 14 -> Color(100, 255, 100) // Light green
            else -> Color(0, 255, 0)            // Bright green
        }
    }
}
