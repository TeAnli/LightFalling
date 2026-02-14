package top.teanli.lightfalling.module.modules.display

import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import top.teanli.lightfalling.event.impl.Render2DEvent
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.ColorTool
import top.teanli.lightfalling.tool.CropGrowthRenderer
import top.teanli.lightfalling.tool.RenderTool
import java.awt.Color

class CropGrowthIndicator : Module(
    "CropGrowthIndicator",
    "Displays growth stage of crops you're looking at",
    ModuleCategory.DISPLAY
) {
    private val show2D = checkbox("show2d", true)
    private val show3D = checkbox("show3d", true)
    private val scanRange = slider("scanrange", 16.0, 4.0, 32.0, 0)
    private val shadow = checkbox("shadow", true)
    private val showPercentage = checkbox("showpercentage", true)

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
                    
                    val cropInfo = CropGrowthRenderer.getCropInfo(blockState) ?: continue
                    
                    CropGrowthRenderer.renderCropText(
                        poseStack,
                        buffer,
                        pos,
                        camera.position(),
                        cropInfo
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
        
        val cropInfo = CropGrowthRenderer.getCropInfo(blockState) ?: return@listen

        // Calculate top-right position
        val screenWidth = mc.window.guiScaledWidth
        val padding = 10
        var currentY = padding
        
        // Collect all text to display
        val lines = mutableListOf<Pair<String, Color>>()

        // Display crop name
        lines.add("Crop: ${cropInfo.name}" to ColorTool.getPercentageColor(100))

        // Display growth stage
        val stageColor = ColorTool.getPercentageColor(cropInfo.percentage)
        lines.add("Stage: ${cropInfo.currentStage}/${cropInfo.maxStage}" to stageColor)

        // Display percentage if enabled
        if (showPercentage.value) {
            lines.add("Growth: ${cropInfo.percentage}%" to stageColor)
        }

        // Display status
        val status = if (cropInfo.isFullyGrown) "Fully Grown" else "Growing"
        val statusColor = if (cropInfo.isFullyGrown) ColorTool.getPercentageColor(100) else ColorTool.getPercentageColor(50)
        lines.add("Status: $status" to statusColor)

        // Render all text (right-aligned)
        for ((text, color) in lines) {
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
