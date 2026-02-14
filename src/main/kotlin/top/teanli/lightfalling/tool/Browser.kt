package top.teanli.lightfalling.tool

import net.ccbluex.liquidbounce.mcef.MCEF
import java.io.IOException
import kotlinx.coroutines.*

object Browser {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun download(){
        try {
            val resourceManager = MCEF.INSTANCE.newResourceManager()

            if (!resourceManager.isSystemCompatible) {
                return
            }

            if (resourceManager.requiresDownload()) {
                scope.launch {
                    try {
                        resourceManager.downloadJcef()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}