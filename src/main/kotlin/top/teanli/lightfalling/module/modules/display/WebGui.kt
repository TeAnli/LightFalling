package top.teanli.lightfalling.module.modules.display

import io.netty.util.ResourceLeakDetector.setEnabled
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.ui.web.WebUI
import top.teanli.lightfalling.ui.web.WebUIScreen

class WebGui : Module("WebGui", "打开基于Web的图形界面", ModuleCategory.DISPLAY) {
    
    init {
        key = GLFW.GLFW_KEY_RIGHT_SHIFT
    }

    override fun onEnable() {
        val mc = MinecraftClient.getInstance()
        // Here you would put your web UI URL. 
        // For testing, we can use a local file or a remote URL.
//        val sampleWebUI = object : WebUI("https://www.baidu.com") {}
//        mc.setScreen(WebUIScreen(sampleWebUI))
//        this.toggle()
    }
}
