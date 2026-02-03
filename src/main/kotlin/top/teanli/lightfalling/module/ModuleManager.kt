package top.teanli.lightfalling.module

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import top.teanli.lightfalling.tool.PackageScanner

/**
 * Module Manager
 * Responsible for automatic scanning, registration, lifecycle management, and querying of modules.
 */
object ModuleManager {
    private val modules = mutableListOf<Module>()

    /**
     * Initializes the module manager.
     */
    fun init() {
        scanModules()

        // Register client tick event to update enabled modules
        ClientTickEvents.END_CLIENT_TICK.register {
            modules.filter { it.isEnabled }.forEach { it.onUpdate() }
        }
    }

    /**
     * Automatically scans and registers modules.
     */
    private fun scanModules() {
        val classes = PackageScanner.scan("top.teanli.lightfalling.module.impl", Module::class.java)
        
        classes.forEach { clazz ->
            try {
                val module = clazz.getDeclaredConstructor().newInstance()
                register(module)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    /**
     * Manually registers a module.
     */
    private fun register(module: Module) {
        modules.add(module)
    }

    /**
     * Gets all registered modules.
     */
    fun getModules(): List<Module> {
        return modules.toList()
    }

    /**
     * Gets modules by category.
     */
    fun getModulesByCategory(category: ModuleCategory): List<Module> {
        return modules.filter { it.category == category }
    }

    /**
     * Gets a module by name (case-insensitive).
     */
    fun getModuleByName(name: String): Module? {
        return modules.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Gets a module instance by its class.
     */
    fun <T : Module> getModule(clazz: Class<T>): T? {
        return modules.find { it.javaClass == clazz } as? T
    }
}