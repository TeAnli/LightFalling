package top.teanli.lightfalling.module

import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.Lightfalling
import top.teanli.lightfalling.config.ConfigSystem
import top.teanli.lightfalling.event.Event
import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.event.EventManager
import top.teanli.lightfalling.event.impl.KeyEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.tool.PackageScanner

/**
 * Module Manager
 * Responsible for automatic scanning, registration, lifecycle management, and querying of modules.
 */
@Suppress("UNUSED_PARAMETER")
object ModuleManager : EventListener {
    private val modules = mutableListOf<Module>()

    override val eventHandlers = mutableListOf<EventListener.EventHandler<out Event>>()
    override val isEventListenerActive: Boolean = true

    private val onKey = listen<KeyEvent> { event ->
        if (event.action == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            modules.forEach { module ->
                if (module.key != 0 && module.key == event.key) {
                    module.toggle()
                }
            }
        }
    }

    /**
     * Initializes the module manager.
     */
    fun init() {
        scanModules()
        EventManager.subscribe(this)
    }

    /**
     * Automatically scans and registers modules.
     */
    private fun scanModules() {
        val packageName = "${javaClass.`package`.name}.modules"
        val classes = PackageScanner.scan(packageName, Module::class.java)
        
        for (moduleClass in classes) {
            try {
                val instance = instantiateModule(moduleClass)
                if (instance != null) {
                    register(instance)
                }
            } catch (e: Throwable) {
                Lightfalling.log.error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
            }
        }
    }

    /**
     * Instantiates a module class, handling both Kotlin objects and regular classes.
     */
    private fun instantiateModule(clazz: Class<out Module>): Module? {
        // Try to get Kotlin object instance first
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            val instance = field.get(null)
            if (instance is Module) return instance
        } catch (e: Exception) {
            // Not a Kotlin object, continue to regular instantiation
            Lightfalling.log.error("Failed to instantiate module: ${clazz.name}", e)
        }

        // Try to instantiate via default constructor
        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            Lightfalling.log.error("Failed to instantiate module: ${clazz.name}", e)
            null
        }
    }

    /**
     * Manually registers a module.
     */
    private fun register(module: Module) {
        Lightfalling.log.info("Registering module {}", module.name)
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
    @Suppress("UNCHECKED_CAST")
    fun <T : Module> getModule(clazz: Class<T>): T? {
        return modules.find { it.javaClass == clazz } as? T
    }
}