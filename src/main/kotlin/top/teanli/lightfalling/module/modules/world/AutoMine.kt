package top.teanli.lightfalling.module.modules.world

import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.module.setting.BlockListSetting

class AutoMine : Module(
    "AutoMine",
    "Automatically mines the block you are looking at",
    ModuleCategory.WORLD
) {
    private val range = slider("Range", 4.5, 1.0, 6.0, 1)
    
    private val blockList = setting(BlockListSetting(
        "Target Blocks",
        mutableSetOf(
            Blocks.STONE,
            Blocks.DEEPSLATE,
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE
        )
    ))
    
    private val mineAll = checkbox("Mine All Blocks", false)
    private val checkTool = checkbox("Check Tool Efficiency", true)

    private val onTick = listen<TickEvent> {
        if (mc.screen != null) return@listen
        val player = mc.player ?: return@listen
        val level = mc.level ?: return@listen
        val interactionManager = mc.gameMode ?: return@listen

        val hitResult = mc.hitResult as? BlockHitResult ?: return@listen
        if (hitResult.type != HitResult.Type.BLOCK) return@listen
        
        val pos = hitResult.blockPos
        val blockState = level.getBlockState(pos)
        if (blockState.isAir) return@listen
        
        // Check if this block should be mined
        if (!mineAll.value && !blockList.value.contains(blockState.block)) {
            return@listen
        }
        
        // Check if tool is sufficient for mining the block
        if (checkTool.value) {
            val heldItem = player.mainHandItem
            
            // Check if block requires correct tool
            if (blockState.requiresCorrectToolForDrops()) {
                // Block requires correct tool, check if current tool is suitable
                if (!heldItem.isCorrectToolForDrops(blockState)) {
                    return@listen
                }
            }
        }

        if (player.eyePosition.distanceTo(pos.bottomCenter) <= range.value) {
            if (mc.options.keyAttack.isDown) return@listen

            if (interactionManager.continueDestroyBlock(pos, hitResult.direction)) {
                player.swing(InteractionHand.MAIN_HAND)
            }
        }
    }
}
