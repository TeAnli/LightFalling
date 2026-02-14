package top.teanli.lightfalling.tool

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.CocoaBlock
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.NetherWartBlock
import net.minecraft.world.level.block.StemBlock
import net.minecraft.world.level.block.SweetBerryBushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

/**
 * Singleton renderer for crop growth indicators
 */
object CropGrowthRenderer {
    private val mc: Minecraft = Minecraft.getInstance()

    data class CropInfo(
        val name: String,
        val currentStage: Int,
        val maxStage: Int
    ) {
        val percentage: Int
            get() = ((currentStage.toDouble() / maxStage) * 100).toInt()

        val isFullyGrown: Boolean
            get() = currentStage >= maxStage
    }

    fun getCropInfo(blockState: BlockState): CropInfo? {
        val block = blockState.block

        return when (block) {
            is CropBlock -> {
                val age = blockState.getValue(CropBlock.AGE)
                val maxAge = block.maxAge
                CropInfo(block.name.string, age, maxAge)
            }
            is StemBlock -> {
                val age = blockState.getValue(StemBlock.AGE)
                CropInfo(block.name.string, age, StemBlock.MAX_AGE)
            }
            is CocoaBlock -> {
                val age = blockState.getValue(CocoaBlock.AGE)
                CropInfo("Cocoa", age, CocoaBlock.MAX_AGE)
            }
            is NetherWartBlock -> {
                val age = blockState.getValue(NetherWartBlock.AGE)
                CropInfo("Nether Wart", age, NetherWartBlock.MAX_AGE)
            }
            is SweetBerryBushBlock -> {
                val age = blockState.getValue(SweetBerryBushBlock.AGE)
                CropInfo("Sweet Berry Bush", age, SweetBerryBushBlock.MAX_AGE)
            }
            else -> null
        }
    }

    fun renderCropText(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        pos: BlockPos,
        cameraPos: Vec3,
        cropInfo: CropInfo
    ) {
        // Determine text and color
        val text = if (cropInfo.isFullyGrown) "✓" else "${cropInfo.percentage}%"
        val color = ColorTool.getPercentageColor(cropInfo.percentage)
        
        Render3DTool.renderText3D(
            poseStack,
            buffer,
            cameraPos,
            pos.x + 0.5,
            pos.y + 0.8,
            pos.z + 0.5,
            text,
            color,
            distanceScale = true
        )
    }
}