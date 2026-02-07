package top.teanli.lightfalling.tool

import top.teanli.lightfalling.event.EventListener
import top.teanli.lightfalling.event.EventManager

object ToolManager {
    private val tools = mutableListOf<Any>()

    fun init() {
        register(TickRateTool)
        // Future tools go here
    }

    private fun register(tool: Any) {
        tools.add(tool)
        if (tool is EventListener) {
            EventManager.subscribe(tool)
        }
    }
}
