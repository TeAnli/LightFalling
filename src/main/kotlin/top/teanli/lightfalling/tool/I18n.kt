package top.teanli.lightfalling.tool

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object I18n {
    private val mc = Minecraft.getInstance()
    
    /**
     * Translate a key to the current language
     */
    fun translate(key: String, vararg args: Any): String {
        return net.minecraft.client.resources.language.I18n.get(key, *args)
    }
    
    /**
     * Get a translated Component
     */
    fun component(key: String, vararg args: Any): Component {
        return Component.translatable(key, *args)
    }
    
    /**
     * Check if a translation key exists
     */
    fun hasKey(key: String): Boolean {
        return net.minecraft.client.resources.language.I18n.exists(key)
    }
    
    /**
     * Get module name translation key
     */
    fun moduleKey(moduleName: String): String {
        return "lightfalling.module.${moduleName.lowercase()}"
    }
    
    /**
     * Get module description translation key
     */
    fun moduleDescKey(moduleName: String): String {
        return "lightfalling.module.${moduleName.lowercase()}.desc"
    }
    
    /**
     * Get setting translation key
     */
    fun settingKey(moduleName: String, settingName: String): String {
        return "lightfalling.module.${moduleName.lowercase()}.${settingName.lowercase().replace(" ", "")}"
    }
    
    /**
     * Get category translation key
     */
    fun categoryKey(categoryName: String): String {
        return "lightfalling.category.${categoryName.lowercase()}"
    }
    
    /**
     * Translate module name
     */
    fun translateModule(moduleName: String): String {
        val key = moduleKey(moduleName)
        return if (hasKey(key)) translate(key) else moduleName
    }
    
    /**
     * Translate module description
     */
    fun translateModuleDesc(moduleName: String, defaultDesc: String): String {
        val key = moduleDescKey(moduleName)
        return if (hasKey(key)) translate(key) else defaultDesc
    }
    
    /**
     * Translate setting name
     */
    fun translateSetting(moduleName: String, settingName: String): String {
        val key = settingKey(moduleName, settingName)
        return if (hasKey(key)) translate(key) else settingName
    }
    
    /**
     * Translate category name
     */
    fun translateCategory(categoryName: String): String {
        val key = categoryKey(categoryName)
        return if (hasKey(key)) translate(key) else categoryName
    }
}
