package top.teanli.lightfalling.module

import net.minecraft.client.MinecraftClient

/**
 * Base class for all modules.
 * Every functional module should inherit from this class.
 */
abstract class Module(
    val name: String,        // Module name
    val description: String, // Module description
    val category: ModuleCategory, // Module category
    var key: Int = 0        // Key binding
) {
    protected val mc: MinecraftClient = MinecraftClient.getInstance()
    var isEnabled: Boolean = false
        private set

    /**
     * Toggles the module's enabled state.
     */
    fun toggle() {
        if (isEnabled) {
            disable()
        } else {
            enable()
        }
    }

    /**
     * Enables the module.
     */
    fun enable() {
        if (!isEnabled) {
            isEnabled = true
            onEnable()
        }
    }

    /**
     * Disables the module.
     */
    fun disable() {
        if (isEnabled) {
            isEnabled = false
            onDisable()
        }
    }

    protected open fun onEnable() {}
    protected open fun onDisable() {}
    
    /**
     * Logic to be executed every tick.
     */
    open fun onUpdate() {}
}