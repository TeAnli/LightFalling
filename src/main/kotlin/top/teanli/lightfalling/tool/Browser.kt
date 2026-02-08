package top.teanli.lightfalling.tool

import net.ccbluex.liquidbounce.mcef.MCEF
import java.io.IOException

object Browser {
    fun download(){
        try {
            val resourceManager = MCEF.INSTANCE.newResourceManager()

            if (!resourceManager.isSystemCompatible) {
                return
            }

            if (resourceManager.requiresDownload()) {
                Multithreading.runAsync {
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