package top.teanli.lightfalling.module

import net.minecraft.client.MinecraftClient
import top.teanli.lightfalling.event.Event
import top.teanli.lightfalling.event.EventListener

/**
 * Base class for all modules.
 * Every functional module should inherit from this class.
 */
abstract class Module(
    val name: String,        // Module name
    val description: String, // Module description
    val category: ModuleCategory, // Module category
    var key: Int = 0        // Key binding
) : EventListener {
    protected val mc: MinecraftClient = MinecraftClient.getInstance()
    var isEnabled: Boolean = false
        private set

    // List of functional event handlers from EventListener
    override val eventHandlers = mutableListOf<EventListener.EventHandler<out Event>>()

    override val isEventListenerActive: Boolean
        get() = isEnabled

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
     * Deprecated: Use onEvent(TickEvent) instead.
     */
    open fun onUpdate() {}
}