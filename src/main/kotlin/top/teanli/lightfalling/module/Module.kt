package top.teanli.lightfalling.module

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.event.Event
import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.module.setting.*
import top.teanli.lightfalling.tool.MessageTool
import java.awt.Color
import java.util.function.Supplier

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

    val settings = mutableListOf<Setting<*>>()
    protected val log = LogManager.getLogger("LightFalling")
    /**
     * Creation helpers for settings
     */
    protected fun checkbox(name: String, defaultValue: Boolean, visibility: Supplier<Boolean> = Supplier { true }): BooleanSetting {
        return BooleanSetting(name, defaultValue, visibility).also { settings.add(it) }
    }

    protected fun slider(name: String, defaultValue: Double, min: Double, max: Double, precision: Int = 1, visibility: Supplier<Boolean> = Supplier { true }): NumberSetting {
        return NumberSetting(name, defaultValue, min, max, precision, visibility).also { settings.add(it) }
    }

    protected fun mode(name: String, defaultValue: String, modes: List<String>, visibility: Supplier<Boolean> = Supplier { true }): ModeSetting {
        return ModeSetting(name, defaultValue, modes, visibility).also { settings.add(it) }
    }

    protected fun color(name: String, defaultValue: Color, rainbow: Boolean = false, visibility: Supplier<Boolean> = Supplier { true }): ColorSetting {
        return ColorSetting(name, defaultValue, rainbow, visibility).also { settings.add(it) }
    }

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
    
}