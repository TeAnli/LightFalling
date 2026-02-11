package top.teanli.lightfalling.module.setting

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.awt.Color
import java.util.function.Supplier

abstract class Setting<T>(
    val name: String,
    var value: T,
    val visibility: Supplier<Boolean> = Supplier { true }
) {
    fun isVisible(): Boolean = visibility.get()
}

class BooleanSetting(
    name: String,
    value: Boolean,
    visibility: Supplier<Boolean> = Supplier { true }
) : Setting<Boolean>(name, value, visibility)

class NumberSetting(
    name: String,
    value: Double,
    val min: Double,
    val max: Double,
    val precision: Int = 1,
    visibility: Supplier<Boolean> = Supplier { true }
) : Setting<Double>(name, value, visibility)

class ModeSetting(
    name: String,
    value: String,
    val modes: List<String>,
    visibility: Supplier<Boolean> = Supplier { true }
) : Setting<String>(name, value, visibility) {
    fun nextMode() {
        val index = modes.indexOf(value)
        value = modes[(index + 1) % modes.size]
    }
}

class ColorSetting(
    name: String,
    value: Color,
    val rainbow: Boolean = false,
    visibility: Supplier<Boolean> = Supplier { true }
) : Setting<Color>(name, value, visibility)

class BlockListSetting(
    name: String,
    value: MutableSet<Block> = mutableSetOf(),
    visibility: Supplier<Boolean> = Supplier { true }
) : Setting<MutableSet<Block>>(name, value, visibility) {
    fun addBlock(block: Block) {
        value.add(block)
    }
    
    fun removeBlock(block: Block) {
        value.remove(block)
    }
    
    fun containsBlock(block: Block): Boolean {
        return value.contains(block)
    }
    
    fun toggleBlock(block: Block) {
        if (containsBlock(block)) {
            removeBlock(block)
        } else {
            addBlock(block)
        }
    }
}
