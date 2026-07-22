package com.drmangotea.tfmg.mixin;

import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Util.class)
public class UtilMixin {

    /**
     * Just silences one specific noisy warning ("setBlock in a far chunk") - everything else
     * should behave exactly like vanilla, including for other mods that also hook this method,
     * so this only cancels the call for that one case instead of replacing the whole method.
     */
    @Inject(method = "logAndPauseIfInIde", at = @At("HEAD"), cancellable = true)
    private static void tfmg$suppressFarChunkWarning(String error, CallbackInfo ci) {
        if (error.contains("Detected setBlock in a far chunk"))
            ci.cancel();
    }

}
