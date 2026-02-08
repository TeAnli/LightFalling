package top.teanli.lightfalling.tool

import kotlinx.coroutines.*

object Multithreading {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun runAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch {
            try {
                block()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun runOnMain(block: () -> Unit) {
        net.minecraft.client.Minecraft.getInstance().execute {
            block()
        }
    }

    fun stop() {
        scope.cancel()
    }
}
