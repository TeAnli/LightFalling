package top.teanli.lightfalling.module

import net.minecraft.client.MinecraftClient

abstract class Module(
    val name: String,
    val description: String,
    val category: ModuleCategory,
    var key: Int = 0
) {
    protected val mc: MinecraftClient = MinecraftClient.getInstance()
    var isEnabled: Boolean = false
        private set

    fun toggle() {
        if (isEnabled) {
            disable()
        } else {
            enable()
        }
    }

    fun enable() {
        if (!isEnabled) {
            isEnabled = true
            onEnable()
        }
    }

    fun disable() {
        if (isEnabled) {
            isEnabled = false
            onDisable()
        }
    }

    protected open fun onEnable() {}
    protected open fun onDisable() {}
    open fun onUpdate() {}
}