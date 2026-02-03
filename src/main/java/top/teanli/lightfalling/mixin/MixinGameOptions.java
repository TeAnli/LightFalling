package top.teanli.lightfalling.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.teanli.lightfalling.module.modules.render.CustomFOV;

@Mixin(GameOptions.class)
public class MixinGameOptions {
    // We can't easily redirect the constructor of ValidatingIntSliderCallbacks inside GameOptions constructor
    // because it's usually an anonymous or inline instantiation.
    
    // However, we can modify the fov SimpleOption after it's created.
}
