package top.teanli.lightfalling.module

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import top.teanli.lightfalling.module.impl.movement.Sprint
import top.teanli.lightfalling.module.impl.render.FullBright

object ModuleManager {
    private val modules = mutableListOf<Module>()

    fun init() {
        register(Sprint())
        register(FullBright())

        ClientTickEvents.END_CLIENT_TICK.register {
            modules.filter { it.isEnabled }.forEach { it.onUpdate() }
        }
    }

    private fun register(module: Module) {
        modules.add(module)
    }

    fun getModules(): List<Module> {
        return modules.toList()
    }

    fun getModulesByCategory(category: ModuleCategory): List<Module> {
        return modules.filter { it.category == category }
    }

    fun getModuleByName(name: String): Module? {
        return modules.find { it.name.equals(name, ignoreCase = true) }
    }

    fun <T : Module> getModule(clazz: Class<T>): T? {
        return modules.find { it.javaClass == clazz } as? T
    }
}