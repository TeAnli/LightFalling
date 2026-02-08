package top.teanli.lightfalling.mixin;

import org.spongepowered.asm.mixin.Mixin;

public class MixinGameOptions {
    // We can't easily redirect the constructor of ValidatingIntSliderCallbacks inside GameOptions constructor
    // because it's usually an anonymous or inline instantiation.
    
    // However, we can modify the fov SimpleOption after it's created.
}
