package top.teanli.lightfalling.tool

import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.Modifier
import java.nio.file.Files

/**
 * Utility class for package scanning.
 * Used to scan classes within a specific package in the Fabric environment.
 */
object PackageScanner {
    /**
     * Scans all classes under the specified package that inherit from targetType.
     *
     * @param basePackage The base package name (e.g., "top.teanli.lightfalling.module.impl")
     * @param targetType The Class object of the target base class or interface
     * @param modId The Mod ID, defaults to "lightfalling"
     * @return A list of scanned classes
     */
    fun <T> scan(basePackage: String, targetType: Class<T>, modId: String = "lightfalling"): List<Class<out T>> {
        val container = FabricLoader.getInstance().getModContainer(modId).orElse(null) ?: return emptyList()
        val basePath = basePackage.replace(".", "/")
        val classes = mutableListOf<Class<out T>>()

        container.findPath(basePath).ifPresent { root ->
            Files.walk(root).use { walk ->
                walk.filter { it.toString().endsWith(".class") }.forEach { path ->
                    val relativePath = root.relativize(path).toString().replace("\\", "/")
                    val className = "$basePackage.${relativePath.replace("/", ".").removeSuffix(".class")}"

                    try {
                        val clazz = Class.forName(className)
                        if (targetType.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.modifiers)) {
                            @Suppress("UNCHECKED_CAST")
                            classes.add(clazz as Class<out T>)
                        }
                    } catch (e: Exception) {
                        //asdasd ignore
                    }
                }
            }
        }
        return classes
    }
}
