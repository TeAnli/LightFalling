package top.teanli.lightfalling.module.setting

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import top.teanli.lightfalling.tool.I18n
import java.awt.Color
import java.util.function.Supplier

abstract class Setting<T>(
    val name: String,
    var value: T,
    val visibility: Supplier<Boolean> = Supplier { true },
    val moduleName: String? = null
) {
    fun isVisible(): Boolean = visibility.get()
    
    /**
     * Get translated setting name
     */
    fun getDisplayName(): String {
        return if (moduleName != null) {
            I18n.translateSetting(moduleName, name)
        } else {
            name
        }
    }
}

class BooleanSetting(
    name: String,
    value: Boolean,
    visibility: Supplier<Boolean> = Supplier { true },
    moduleName: String? = null
) : Setting<Boolean>(name, value, visibility, moduleName)

class NumberSetting(
    name: String,
    value: Double,
    val min: Double,
    val max: Double,
    val precision: Int = 1,
    visibility: Supplier<Boolean> = Supplier { true },
    moduleName: String? = null
) : Setting<Double>(name, value, visibility, moduleName)

class ModeSetting(
    name: String,
    value: String,
    val modes: List<String>,
    visibility: Supplier<Boolean> = Supplier { true },
    moduleName: String? = null
) : Setting<String>(name, value, visibility, moduleName) {
    fun nextMode() {
        val index = modes.indexOf(value)
        value = modes[(index + 1) % modes.size]
    }

    /**
     * Get translated mode name
     */
    fun getModeDisplayName(): String {
        if (moduleName == null) return value
        val key = "lightfalling.module.${moduleName.lowercase()}.mode.${value.lowercase()}"
        return if (I18n.hasKey(key)) I18n.translate(key) else value
    }
}

class ColorSetting(
    name: String,
    value: Color,
    val rainbow: Boolean = false,
    visibility: Supplier<Boolean> = Supplier { true },
    moduleName: String? = null
) : Setting<Color>(name, value, visibility, moduleName)

class BlockListSetting(
    name: String,
    value: MutableSet<Block> = mutableSetOf(),
    visibility: Supplier<Boolean> = Supplier { true },
    moduleName: String? = null
) : Setting<MutableSet<Block>>(name, value, visibility, moduleName) {
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
