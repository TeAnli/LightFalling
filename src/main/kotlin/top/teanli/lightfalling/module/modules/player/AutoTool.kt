package top.teanli.lightfalling.module.modules.player

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import top.teanli.lightfalling.event.impl.AttackEvent
import top.teanli.lightfalling.event.impl.ClickBlockEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class AutoTool : Module(
    "AutoTool",
    "Automatically switches to the best tool in your hotbar",
    ModuleCategory.PLAYER
) {
    private val switchBack = checkbox("switchback", true)
    private val onMining = checkbox("mining", true)
    private val onAttack = checkbox("attack", true)

    private var lastSlot = -1

    val onClickBlock = listen<ClickBlockEvent> { event ->
        if (!onMining.value) return@listen
        
        val player = mc.player ?: return@listen
        val world = mc.level ?: return@listen
        val state = world.getBlockState(event.pos)
        
        val bestSlot = findBestToolForBlock(state)
        if (bestSlot != -1 && bestSlot != player.inventory.selectedSlot) {
            if (switchBack.value) lastSlot = player.inventory.selectedSlot
            player.inventory.selectedSlot = bestSlot
        }
    }

    val onAttackEntity = listen<AttackEvent> { event ->
        if (!onAttack.value) return@listen
        
        val player = mc.player ?: return@listen
        val target = event.target as? LivingEntity ?: return@listen
        
        val bestSlot = findBestToolForEntity(target)
        if (bestSlot != -1 && bestSlot != player.inventory.selectedSlot) {
            if (switchBack.value) lastSlot = player.inventory.selectedSlot
            player.inventory.selectedSlot = bestSlot
        }
    }

    private fun findBestToolForBlock(state: BlockState): Int {
        val player = mc.player ?: return -1
        var bestSpeed = player.mainHandItem.getDestroySpeed(state)
        var bestSlot = -1

        for (i in 0..8) {
            val stack = player.inventory.getItem(i)
            if (stack.isEmpty) continue
            
            val speed = stack.getDestroySpeed(state)
            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }
        return bestSlot
    }

    private fun findBestToolForEntity(target: LivingEntity): Int {
        val player = mc.player ?: return -1
        
        var bestDamage = getAttackDamage(player.mainHandItem, target)
        var bestSlot = -1

        for (i in 0..8) {
            val stack = player.inventory.getItem(i)
            if (stack.isEmpty) continue
            
            val damage = getAttackDamage(stack, target)
            if (damage > bestDamage) {
                bestDamage = damage
                bestSlot = i
            }
        }
        return bestSlot
    }

    private fun getAttackDamage(stack: ItemStack, target: LivingEntity): Float {
        // Simplified damage check for tool switching in 1.21.1
        // We look at the base attack damage attribute if present
        return stack.item.components().get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS)
            ?.modifiers?.filter { it.attribute == Attributes.ATTACK_DAMAGE }
            ?.sumOf { it.modifier.amount }?.toFloat() ?: 0f
    }

    override fun onDisable() {
        lastSlot = -1
    }
}
