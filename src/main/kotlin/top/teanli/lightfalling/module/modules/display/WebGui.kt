package top.teanli.lightfalling.module.modules.display

import net.minecraft.client.Minecraft
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
        val mc = Minecraft.getInstance()
        val webUI = WebUI("https://google.com")
        mc.setScreen(WebUIScreen(webUI))
        disable()
    }


}
