package top.teanli.lightfalling.ui.web

import net.ccbluex.liquidbounce.mcef.MCEF
import net.ccbluex.liquidbounce.mcef.cef.MCEFBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.browser.CefMessageRouter

abstract class WebUI(val url: String) {
    var browser: MCEFBrowser? = null
    private var messageRouter: CefMessageRouter? = null

    init {
        val mcef = MCEF.INSTANCE
        if (mcef.initialize()) {
            browser = mcef.createBrowser(url, true, null)

            // Setup message router for JS bridge
            val config = CefMessageRouter.CefMessageRouterConfig("mcefQuery", "mcefQueryCancel")
            messageRouter = CefMessageRouter.create(config)
            messageRouter?.addHandler(WebQueryHandler(), true)
            browser?.client?.addMessageRouter(messageRouter)

            // Register load handler for logging/debugging
            browser?.client?.addLoadHandler(object : CefLoadHandlerAdapter() {
                override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                    MCEF.INSTANCE.LOGGER.info("WebUI loaded: $url with status $httpStatusCode")
                }
            })
        }
    }

    fun resize(width: Int, height: Int) {
        browser?.resize(width, height)
    }

    fun close() {
        browser?.close(true)
        browser = null
    }
}
