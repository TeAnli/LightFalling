package top.teanli.lightfalling.tool

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BellBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.ComparatorBlock
import net.minecraft.world.level.block.DaylightDetectorBlock
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.LecternBlock
import net.minecraft.world.level.block.LeverBlock
import net.minecraft.world.level.block.LightningRodBlock
import net.minecraft.world.level.block.NoteBlock
import net.minecraft.world.level.block.ObserverBlock
import net.minecraft.world.level.block.PressurePlateBlock
import net.minecraft.world.level.block.RedStoneOreBlock
import net.minecraft.world.level.block.RedStoneWireBlock
import net.minecraft.world.level.block.RedstoneLampBlock
import net.minecraft.world.level.block.RedstoneTorchBlock
import net.minecraft.world.level.block.RepeaterBlock
import net.minecraft.world.level.block.TargetBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.TripWireBlock
import net.minecraft.world.level.block.TripWireHookBlock
import net.minecraft.world.level.block.WeightedPressurePlateBlock
import net.minecraft.world.level.block.piston.PistonBaseBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

/**
 * Singleton renderer for redstone signal indicators
 */
object RedstoneRenderer {
    private val mc: Minecraft = Minecraft.getInstance()

    data class RedstoneInfo(
        val signal: Int,
        val directSignal: Int,
        val wireSignal: Int?
    ) {
        val displaySignal: Int
            get() = wireSignal ?: signal.coerceAtLeast(directSignal)
    }

    fun isRedstoneBlock(block: Block): Boolean {
        return block is RedStoneWireBlock ||
            block is RedstoneTorchBlock || block is RepeaterBlock || block is ComparatorBlock || block is RedstoneLampBlock || block is ObserverBlock || block is PistonBaseBlock || block is DispenserBlock || block is HopperBlock || block is DoorBlock || block is TrapDoorBlock || block is FenceGateBlock || block is NoteBlock || block is RedStoneOreBlock || block is LeverBlock || block is ButtonBlock || block is PressurePlateBlock || block is WeightedPressurePlateBlock || block is TripWireBlock || block is TripWireHookBlock || block is DaylightDetectorBlock || block is TargetBlock || block is LightningRodBlock || block is LecternBlock || block is BellBlock
    }

    fun getRedstoneInfo(blockState: BlockState, pos: BlockPos): RedstoneInfo? {
        val block = blockState.block
        if (!isRedstoneBlock(block)) return null

        val level = mc.level ?: return null

        val signal = level.getBestNeighborSignal(pos)
        val directSignal = level.getDirectSignalTo(pos)

        val wireSignal = if (block is RedStoneWireBlock) {
            blockState.getValue(RedStoneWireBlock.POWER)
        } else null

        return RedstoneInfo(signal, directSignal, wireSignal)
    }

    fun renderRedstoneText(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        pos: BlockPos,
        cameraPos: Vec3,
        redstoneInfo: RedstoneInfo
    ) {
        val signal = redstoneInfo.displaySignal
        val text = signal.toString()
        val color = ColorTool.getSignalColor(signal)
        
        Render3DTool.renderText3D(
            poseStack,
            buffer,
            cameraPos,
            pos.x + 0.5,
            pos.y + 0.5,
            pos.z + 0.5,
            text,
            color,
        )
    }
}