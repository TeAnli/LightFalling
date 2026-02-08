package top.teanli.lightfalling.ui.web

import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefMessageRouterHandlerAdapter
import top.teanli.lightfalling.module.ModuleManager

class WebQueryHandler : CefMessageRouterHandlerAdapter() {
    override fun onQuery(
        browser: CefBrowser?,
        frame: CefFrame?,
        queryId: Long,
        request: String?,
        persistent: Boolean,
        callback: CefQueryCallback?
    ): Boolean {
        if (request == null) return false

        try {
            if (request.startsWith("toggleModule:")) {
                val moduleName = request.substringAfter("toggleModule:")
                val module = ModuleManager.getModuleByName(moduleName)
                if (module != null) {
                    module.toggle()
                    callback?.success("Toggled module: ${module.name}")
                } else {
                    callback?.failure(404, "Module not found")
                }
                return true
            }

            if (request == "getModules") {
                val modules = ModuleManager.getModules().map {
                    """{"name": "${it.name}", "enabled": ${it.state}, "category": "${it.category}"}"""
                }
                callback?.success("[${modules.joinToString(",")}]")
                return true
            }
        } catch (e: Exception) {
            callback?.failure(500, e.message ?: "Unknown error")
            return true
        }

        return false
    }
}
