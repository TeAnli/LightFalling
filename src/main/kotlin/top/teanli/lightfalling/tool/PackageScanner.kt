package top.teanli.lightfalling.tool

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil
import java.lang.reflect.Modifier
import java.net.URI

/**
 * Utility class for package scanning.
 * Used to scan classes within a specific package in the Fabric environment.
 */
object PackageScanner {

    /**
     * Scans for classes in a given package that are assignable to the specified class.
     *
     * @param packagePath The package to scan (e.g., "top.teanli.lightfalling.module.modules")
     * @param klass The base class or interface to filter by
     * @return A list of classes that are assignable to [klass], excluding interfaces and abstract classes
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> scan(packagePath: String, klass: Class<T>): List<Class<out T>> {
        val resolver = ResolverUtil()
        resolver.classLoader = klass.classLoader
        resolver.findInPackage(object : ResolverUtil.Test {
            override fun matches(type: Class<*>): Boolean {
                return true
            }

            override fun matches(resource: URI?): Boolean {
                return true
            }

            override fun doesMatchClass(): Boolean {
                return true
            }

            override fun doesMatchResource(): Boolean {
                return false
            }
        }, packagePath)
        val classes = mutableListOf<Class<out T>>()

        for (resolved in resolver.classes) {
            resolved.declaredMethods.find {
                Modifier.isNative(it.modifiers)
            }?.let {
                val klass1 = it.declaringClass.typeName + "." + it.name
                throw UnsatisfiedLinkError(klass1 + "\n\tat ${klass1}(Native Method)")
            }
            if (
                klass.isAssignableFrom(resolved)
                && !resolved.isInterface
                && !Modifier.isAbstract(resolved.modifiers)
            ) {
                classes.add(resolved as Class<out T>)
            }
        }

        return classes
    }
}
