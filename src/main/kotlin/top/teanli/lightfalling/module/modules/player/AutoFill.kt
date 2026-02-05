package top.teanli.lightfalling.module.modules.player

import net.minecraft.screen.slot.SlotActionType
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class AutoFill : Module("AutoFill", "Automatically refills items in your hotbar", ModuleCategory.PLAYER) {

    private val threshold = slider("Threshold", 8.0, 1.0, 64.0, 0)
    private val tickDelay = slider("TickDelay", 2.0, 1.0, 20.0, 0)
    
    private var delayTimer = 0

    private val onTick = listen<TickEvent> {
        if (mc.currentScreen != null) return@listen

        if (delayTimer > 0) {
            delayTimer--
            return@listen
        }

        val player = mc.player ?: return@listen
        val inventory = player.inventory

        // Check hotbar slots (0-8)
        for (i in 0..8) {
            val stack = inventory.getStack(i)

            // If slot is empty or below threshold (and stackable)
            if (stack.isEmpty || (stack.isStackable && stack.count <= threshold.value && stack.count < stack.maxCount)) {
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
    private fun findReplacement(targetItem: net.minecraft.item.Item?, hotbarSlot: Int): Int {
        val inventory = mc.player?.inventory ?: return -1
        
        // If we know what item was there, look for the same item
        if (targetItem != null) {
            for (i in 9..35) {
                val stack = inventory.getStack(i)
                if (!stack.isEmpty && stack.item == targetItem) {
                    return i
                }
            }
        }
        
        return -1
    }

    private fun fillSlot(fromSlot: Int, toSlot: Int) {
        val player = mc.player ?: return
        val interactionManager = mc.interactionManager ?: return
        val syncId = player.currentScreenHandler.syncId
        
        // Convert inventory slot index to ScreenHandler slot index
        // Player inventory slots in GenericContainer/Inventory screen:
        // 0-8: Hotbar (mapped to 36-44 in PlayerScreenHandler)
        // 9-35: Main inventory (mapped to 9-35 in PlayerScreenHandler)
        // Note: In 1.21.1 PlayerScreenHandler, hotbar is 36-44, main is 9-35.
        
        val fromScreenSlot = fromSlot // 9-35 matches
        val toScreenSlot = toSlot + 36 // 0-8 -> 36-44
        
        // Quick move (Shift-Click) doesn't guarantee destination, so we use PICKUP (Click)
        // Or swap: click from, then click to
        
        interactionManager.clickSlot(syncId, fromScreenSlot, 0, SlotActionType.PICKUP, player)
        interactionManager.clickSlot(syncId, toScreenSlot, 0, SlotActionType.PICKUP, player)
        
        // If there's still item on cursor, put it back to the original slot
        interactionManager.clickSlot(syncId, fromScreenSlot, 0, SlotActionType.PICKUP, player)
    }

    override fun onEnable() {
        delayTimer = 0
    }
}
