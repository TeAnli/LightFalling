package top.teanli.lightfalling.module

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting
import top.teanli.lightfalling.event.Event
import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.tool.MessageTool

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
    var state: Boolean = false
        private set

    // List of functional event handlers from EventListener
    override val eventHandlers = mutableListOf<EventListener.EventHandler<out Event>>()

    override val isEventListenerActive: Boolean
        get() = state

    /**
     * Toggles the module's enabled state.
     */
    fun toggle() {
        if (state) {
            disable()
        } else {
            enable()
        }
    }

    /**
     * Enables the module.
     */
    fun enable() {
        if (!state) {
            state = true
            onEnable()
            MessageTool.sendRaw("Enabled ${Formatting.GREEN}$name")
        }
    }

    /**
     * Disables the module.
     */
    fun disable() {
        if (state) {
            state = false
            onDisable()
            MessageTool.sendRaw("Disabled ${Formatting.RED}$name")
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