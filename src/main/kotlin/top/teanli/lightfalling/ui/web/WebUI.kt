package top.teanli.lightfalling.ui.web

import net.ccbluex.liquidbounce.mcef.MCEF
import net.ccbluex.liquidbounce.mcef.MCEFPlatform
import net.ccbluex.liquidbounce.mcef.cef.MCEFBrowser
import org.cef.browser.CefMessageRouter
import org.cef.browser.CefMessageRouter.CefMessageRouterConfig
import top.teanli.lightfalling.tool.Multithreading
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class WebUI(val url: String) {
    var browser: MCEFBrowser? = null
    private var messageRouter: CefMessageRouter? = null
    var unSupport: Boolean = false

    init {
        // Ensure MCEF is initialized when the first WebUI is created
        MCEF.INSTANCE.initialize()

        if (browser == null) {
            val transparent = true
            browser = MCEF.INSTANCE.createBrowser(url, transparent, null)
            
            // Initialize Message Router
            val config = CefMessageRouterConfig("mcefQuery", "mcefQueryCancel")
            messageRouter = CefMessageRouter.create(config)
            messageRouter?.addHandler(WebQueryHandler(), true)
            
            // Register router to the browser's client
            browser?.client?.addMessageRouter(messageRouter)
            
            this.resize(1280, 720)
        }
    }

    fun resize(width: Int, height: Int) {
        browser?.resize(width, height)
    }

    fun close() {
        browser?.client?.removeMessageRouter(messageRouter)
        messageRouter?.dispose()
        messageRouter = null
        
        browser?.close(true)
        browser = null
        
        if (MCEFPlatform.getPlatform().isWindows) {
            val processName = "jcef_helper.exe"
            try {
                val processBuilder = ProcessBuilder("tasklist")
                val process = processBuilder.start()

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                var isRunning = false
                while ((reader.readLine().also { line = it }) != null) {
                    if (line!!.contains(processName)) {
                        isRunning = true
                        break
                    }
                }
                reader.close()

                if (isRunning) {
                    MCEF.INSTANCE.logger.warn("JCEF is still running, killing to avoid lingering processes.")
                    val killProcess = ProcessBuilder("taskkill", "/F", "/IM", processName)
                    killProcess.start()
                }
            } catch (e: Exception) {
                MCEF.INSTANCE.logger.error("Unable to check if JCEF is still running.", e)
            }
        }
    }

}
