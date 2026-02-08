package top.teanli.lightfalling.module.modules.player

import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Item
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class AutoFill : Module("AutoFill", "Automatically refills items in your hotbar", ModuleCategory.PLAYER) {

    private val threshold = slider("Threshold", 8.0, 1.0, 64.0, 0)
    private val tickDelay = slider("TickDelay", 2.0, 1.0, 20.0, 0)
    
    private var delayTimer = 0

    private val onTick = listen<TickEvent> {
        if (mc.screen != null) return@listen

        if (delayTimer > 0) {
            delayTimer--
            return@listen
        }

        val player = mc.player ?: return@listen
        val inventory = player.inventory

        // Check hotbar slots (0-8)
        for (i in 0..8) {
            val stack = inventory.getItem(i)

            // If slot is empty or below threshold (and stackable)
            if (stack.isEmpty || (stack.isStackable && stack.count <= threshold.value && stack.count < stack.maxStackSize)) {
                val targetItem = if (stack.isEmpty) null else stack.item

                // Look for matching item in main inventory (9-35)
                val replacementSlot = findReplacement(targetItem, i)

                if (replacementSlot != -1) {
                    fillSlot(replacementSlot, i)
                    delayTimer = tickDelay.value.toInt()
                    return@listen // Only fill one slot per delay to look more natural
                }
            }
        }
    }

    /**
     * Finds a replacement item in the inventory.
     * If targetItem is null, it might be harder to guess what the user wants, 
     * but usually AutoFill is used when an item just ran out.
     */
    private fun findReplacement(targetItem: Item?, hotbarSlot: Int): Int {
        val inventory = mc.player?.inventory ?: return -1
        
        // If we know what item was there, look for the same item
        if (targetItem != null) {
            for (i in 9..35) {
                val stack = inventory.getItem(i)
                if (!stack.isEmpty && stack.item == targetItem) {
                    return i
                }
            }
        }
        
        return -1
    }

    private fun fillSlot(fromSlot: Int, toSlot: Int) {
        val player = mc.player ?: return
        val gameMode = mc.gameMode ?: return
        val containerId = player.containerMenu.containerId
        
        val fromScreenSlot = fromSlot // 9-35 matches
        val toScreenSlot = toSlot + 36 // 0-8 -> 36-44
        
        gameMode.handleInventoryMouseClick(containerId, fromScreenSlot, 0, ClickType.PICKUP, player)
        gameMode.handleInventoryMouseClick(containerId, toScreenSlot, 0, ClickType.PICKUP, player)
        
        gameMode.handleInventoryMouseClick(containerId, fromScreenSlot, 0, ClickType.PICKUP, player)
    }

    override fun onEnable() {
        delayTimer = 0
    }
}
