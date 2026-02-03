package top.teanli.lightfalling.module

import top.teanli.lightfalling.event.Event
import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.event.EventManager
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
    }

    /**
     * Automatically scans and registers modules.
     */
    private fun scanModules() {
        val classes = PackageScanner.scan("top.teanli.lightfalling.module.modules", Module::class.java)
        
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
        // Automatically subscribe to event manager
        EventManager.subscribe(module)
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